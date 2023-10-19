package com.stonewu.sitepush.strategy;


import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.stonewu.sitepush.DefaultSettingFetcher;
import com.stonewu.sitepush.setting.GooglePushSetting;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import lombok.AllArgsConstructor;
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
    public static final String SCOPES = "https://www.googleapis.com/auth/indexing";

    public static final String PUSH_ENDPOINT =
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
                response = update(siteUrl + pageLink, googlePushSetting.getCredentialsJson());
                log.info("Pushing to google webmasters result: {}", response.parseAsString());
            } catch (IOException e) {
                log.info("Push exception: {}", e.getMessage());
                return 0;
            }
            return response.isSuccessStatusCode() ? 1 : 0;
        }
        return -1;
    }

    public HttpResponse update(String url, String credentialsJson)
        throws IOException {
        GoogleCredentials credential = getGoogleCredential(credentialsJson, SCOPES);
        NetHttpTransport netHttpTransport = new NetHttpTransport();
        netHttpTransport.supportsMethod(HttpMethods.POST);

        HashMap<String, String> body = new HashMap<>();
        body.put("type", UPDATE_TYPE);
        body.put("url", url);
        HttpContent content = new JsonHttpContent(new GsonFactory(), body);

        return publish(credential, netHttpTransport, content);
    }

    public HttpResponse publish(GoogleCredentials credential, HttpTransport httpTransport,
        HttpContent httpContent) throws IOException {
        GenericUrl genericUrl = new GenericUrl(PUSH_ENDPOINT);
        HttpRequestFactory httpRequestFactory = httpTransport.createRequestFactory();
        HttpRequest httpRequest = httpRequestFactory.buildPostRequest(genericUrl, httpContent);
        HttpRequestInitializer httpRequestInitializer = new HttpCredentialsAdapter(credential);
        httpRequestInitializer.initialize(httpRequest);
        return httpRequest.execute();
    }

    private GoogleCredentials getGoogleCredential(String credentialsJson,
        String scopes)
        throws IOException {
        InputStream inputStream =
            new ByteArrayInputStream(credentialsJson.getBytes(StandardCharsets.UTF_8));
        return getGoogleCredential(inputStream, scopes);
    }

    private GoogleCredentials getGoogleCredential(InputStream in,
        String scopes)
        throws IOException {
        return GoogleCredentials.fromStream(in)
            .createScoped(Collections.singleton(scopes));
    }
}
