package com.stonewu.sitepush.strategy;


import com.stonewu.sitepush.setting.GooglePushSetting;
import com.stonewu.sitepush.setting.GooglePushSettingProvider;
import com.stonewu.sitepush.setting.PushSettingProvider;
import com.stonewu.sitepush.utils.HttpResponse;
import com.stonewu.sitepush.utils.JWTSignUtil;
import com.stonewu.sitepush.utils.PemReader;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.infra.utils.JsonUtils;
import run.halo.app.plugin.SettingFetcher;

/**
 * @author Erzbir
 * @Date 2023/10/12
 */
@Component
@Slf4j
public class GooglePushStrategy extends AbstractPushStrategy implements PushStrategy {

    public static final String GLOBAL_ENDPOINT =
        "https://indexing.googleapis.com/batch";

    public static final String API_URL = "/v3/urlNotifications:publish";

    public static final String UPDATE_TYPE = "URL_UPDATED";

    public GooglePushStrategy(SettingFetcher settingFetcher, ReactiveExtensionClient client) {
        super(settingFetcher, client);
    }

    @Override
    public String getPushType() {
        return "google";
    }

    @Override
    protected PushSettingProvider getSettingProvider() {
        GooglePushSetting googlePushSetting =
            settingFetcher.fetch(GooglePushSetting.GROUP, GooglePushSetting.class)
                .orElseGet(GooglePushSetting::new);
        return new GooglePushSettingProvider(googlePushSetting);
    }

    @Override
    protected Mono<HttpResponse> request(PushSettingProvider settingProvider, String siteUrl,
        String... pageLinks) throws Exception {
        String[] pushBodyUrls = new String[pageLinks.length];
        List<Map<String, String>> requests = new ArrayList<>();
        for (int i = 0; i < pageLinks.length; i++) {
            String url = siteUrl + pageLinks[i];
            pushBodyUrls[i] = url;
            Map<String, String> map = new HashMap<>();
            map.put("url", url);
            map.put("type", UPDATE_TYPE);
            requests.add(map);
        }
        String pushUrls = Arrays.toString(pushBodyUrls);
        log.info("Pushing to google webmasters: {}", pushUrls);
        String token = GooglePushTokenCreator.createToken(
            JsonUtils.jsonToObject(settingProvider.getAccess(),
                GooglePushTokenCreator.ServiceAccountCredentials.class),
            URI.create(GLOBAL_ENDPOINT));
        String boundary = "===============7330845974216740156==";
        String batchBody = buildBatchBody(requests, boundary);
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaderNames.AUTHORIZATION, "Bearer " + token);
        headers.add(HttpHeaderNames.CONTENT_TYPE, "multipart/mixed; boundary=" + boundary);
        headers.add(HttpHeaderNames.ACCEPT, "application/json");
        return httpRequestSender.request(GLOBAL_ENDPOINT, HttpMethod.POST, headers, batchBody);
    }


    private String buildBatchBody(List<Map<String, String>> requests, String boundary) {
        StringBuilder batchBody = new StringBuilder();
        int contentId = 1;
        for (Map<String, String> request : requests) {
            String requestStr = JsonUtils.objectToJson(request);
            batchBody.append("--").append(boundary).append("\n")
                .append("Content-Type: application/http\n")
                .append("Content-Transfer-Encoding: binary\n")
                .append("Content-ID: ").append(contentId++).append("\n")
                .append("\n")
                .append("POST").append(" ").append(API_URL).append("\n")
                .append("Content-Type: application/json").append("\n")
                .append("accept: application/json").append("\n")
                .append("\n")
                .append(requestStr).append("\n");
        }
        batchBody.append("--").append(boundary).append("--");
        return batchBody.toString();
    }

    private static class GooglePushTokenCreator {

        public static String createToken(ServiceAccountCredentials credentials, URI endpoint)
            throws IOException,
            GeneralSecurityException {
            String privateKeyId = credentials.private_key_id;
            String privateKey = credentials.private_key;
            Map<String, Object> header = new HashMap<>();
            header.put("alg", "RS256");
            header.put("kid", privateKeyId);
            header.put("typ", "JWT");

            Map<String, Object> payload = new HashMap<>();
            String clientEmail = credentials.client_email;
            long l = System.currentTimeMillis();
            payload.put("iss", clientEmail);
            payload.put("aud", getUriForSelfSignedJWT(endpoint));
            payload.put("sub", clientEmail);
            payload.put("iat", l / 1000);
            payload.put("exp", l / 1000 + 3600);
            return JWTSignUtil.getInstance()
                .signUsingRsaSha256(PemReader.getInstance().getPrivateKeyFromPEM(privateKey, "RSA"),
                    header,
                    payload);
        }

        private static URI getUriForSelfSignedJWT(URI uri) {
            if (uri == null || uri.getScheme() == null || uri.getHost() == null) {
                return uri;
            }
            try {
                return new URI(uri.getScheme(), uri.getHost(), "/", null);
            } catch (URISyntaxException unused) {
                return uri;
            }
        }

        @Data
        public static class ServiceAccountCredentials {
            private String type;
            private String project_id;
            private String private_key_id;
            private String private_key;
            private String client_email;
            private String client_id;
            private String auth_uri;
            private String token_uri;
            private String auth_provider_x509_cert_url;
            private String client_x509_cert_url;
            private String universe_domain;
        }
    }

    @Data
    static class GooglePushBody {
        private String url;
        private String type;
    }
}
