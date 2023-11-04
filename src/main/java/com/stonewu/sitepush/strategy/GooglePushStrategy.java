package com.stonewu.sitepush.strategy;


import com.stonewu.sitepush.DefaultSettingFetcher;
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
import java.util.HashMap;
import java.util.Map;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.infra.utils.JsonUtils;

/**
 * @author Erzbir
 * @Date 2023/10/12
 */
@Component
@Slf4j
public class GooglePushStrategy extends AbstractPushStrategy implements PushStrategy {

    public static final String PUSH_ENDPOINT =
        "https://indexing.googleapis.com/v3/urlNotifications:publish";
    public static final String UPDATE_TYPE = "URL_UPDATED";

    public GooglePushStrategy(DefaultSettingFetcher settingFetcher) {
        super(settingFetcher);
    }

    @Override
    public String getPushType() {
        return "google";
    }

    @Override
    protected PushSettingProvider getSettingProvider() {
        GooglePushSetting googlePushSetting = settingFetcher.fetch(
                GooglePushSetting.CONFIG_MAP_NAME,
                GooglePushSetting.GROUP, GooglePushSetting.class)
            .orElseGet(GooglePushSetting::new);
        return new GooglePushSettingProvider(googlePushSetting);
    }

    @Override
    protected Mono<HttpResponse> request(String siteUrl, String pageLink,
        PushSettingProvider settingProvider) throws Exception {
        String pushBodyUrl = siteUrl + pageLink;
        log.info("Pushing to google webmasters: {}", pushBodyUrl);
        String token = GooglePushTokenCreator.createToken(
            JsonUtils.jsonToObject(settingProvider.getAccess(),
                GooglePushTokenCreator.ServiceAccountCredentials.class),
            URI.create(PUSH_ENDPOINT));
        return publish(pushBodyUrl, token);
    }

    public Mono<HttpResponse> publish(String url, String token) {
        GooglePushBody body = new GooglePushBody();
        body.setType(UPDATE_TYPE);
        body.setUrl(url);
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(HttpHeaderNames.AUTHORIZATION, "Bearer " + token);
        return httpRequestSender.request(PUSH_ENDPOINT, HttpMethod.POST, headers,
            JsonUtils.objectToJson(body));
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
