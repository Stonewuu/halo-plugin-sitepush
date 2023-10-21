package com.stonewu.sitepush.strategy;


import cn.hutool.core.lang.Dict;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.stonewu.sitepush.DefaultSettingFetcher;
import com.stonewu.sitepush.setting.GooglePushSetting;
import com.stonewu.sitepush.utils.JWTSignUtil;
import com.stonewu.sitepush.utils.PemReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Erzbir
 * @Date 2023/10/12
 */
@Component
@AllArgsConstructor
@Slf4j
public class GooglePushStrategy implements PushStrategy {
    private DefaultSettingFetcher defaultSettingFetcher;
    public static final String PUBLISH_ENDPOINT =
        "https://indexing.googleapis.com/v3/urlNotifications:publish";

    public static final String UPDATE_TYPE = "URL_UPDATED";

    @Override
    public String getPushType() {
        return "google";
    }

    @Override
    public int push(String siteUrl, String key, String pageLink) {
        GooglePushSetting googlePushSetting = defaultSettingFetcher.fetch(
                GooglePushSetting.CONFIG_MAP_NAME,
                GooglePushSetting.GROUP, GooglePushSetting.class)
            .orElseGet(GooglePushSetting::new);
        if (googlePushSetting.getGoogleEnable() && StringUtils.hasText(
            googlePushSetting.getCredentialsJson())) {
            HttpResponse response;
            try {
                log.info("Pushing to google webmasters: {}", siteUrl + pageLink);
                String token = GooglePushTokenCreator.createToken(
                    JSONUtil.toBean(googlePushSetting.getCredentialsJson(),
                        GooglePushTokenCreator.ServiceAccountCredentials.class),
                    URI.create(PUBLISH_ENDPOINT));
                response = publish(siteUrl + pageLink, token);
                log.info("Pushing to google webmasters result: {}", response.body());
                response.close();
            } catch (GeneralSecurityException | IOException e) {
                log.info("Push exception: {}", e.getMessage());
                return 0;
            }
            return response.isOk() ? 1 : 0;
        }
        return -1;
    }

    public HttpResponse publish(String url, String token) throws IOException {
        GooglePushBody body = new GooglePushBody();
        body.setType(UPDATE_TYPE);
        body.setUrl(url);
        HttpRequest request = HttpRequest
            .post(PUBLISH_ENDPOINT)
            .bearerAuth(token)
            .body(JSONUtil.toJsonStr(body));
        return request.execute();
    }

    private static class GooglePushTokenCreator {

        public static String createToken(ServiceAccountCredentials credentials, URI endpoint)
            throws IOException,
            GeneralSecurityException {
            String privateKeyId = credentials.private_key_id;
            String privateKey = credentials.private_key;
            Dict header = Dict.create();
            header.set("alg", "RS256")
                .set("kid", privateKeyId)
                .set("typ", "JWT");

            Dict payload = Dict.create();
            String clientEmail = credentials.client_email;
            long l = System.currentTimeMillis();
            payload.set("iss", clientEmail)
                .set("aud", getUriForSelfSignedJWT(endpoint))
                .set("sub", clientEmail)
                .set("iat", l / 1000)
                .set("exp", l / 1000 + 3600);
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
