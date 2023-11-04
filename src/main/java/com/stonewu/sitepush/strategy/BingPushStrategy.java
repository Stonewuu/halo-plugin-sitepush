package com.stonewu.sitepush.strategy;

import com.stonewu.sitepush.DefaultSettingFetcher;
import com.stonewu.sitepush.setting.BingPushSetting;
import com.stonewu.sitepush.setting.BingPushSettingProvider;
import com.stonewu.sitepush.setting.PushSettingProvider;
import com.stonewu.sitepush.utils.HttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.infra.utils.JsonUtils;

@Component
@Slf4j
public class BingPushStrategy extends AbstractPushStrategy implements PushStrategy {
    public static final String PUSH_ENDPOINT =
        "https://ssl.bing.com/webmaster/api.svc/json/SubmitUrlbatch?apikey=%s";

    public BingPushStrategy(DefaultSettingFetcher settingFetcher) {
        super(settingFetcher);
    }

    @Override
    public String getPushType() {
        return "bing";
    }

    @Override
    protected PushSettingProvider getSettingProvider() {
        BingPushSetting bingPushSetting =
            settingFetcher.fetch(BingPushSetting.CONFIG_MAP_NAME, BingPushSetting.GROUP,
                BingPushSetting.class).orElseGet(BingPushSetting::new);
        return new BingPushSettingProvider(bingPushSetting);
    }

    @Override
    protected Mono<HttpResponse> request(String siteUrl, String pageLink,
        PushSettingProvider settingProvider)
        throws IOException, ExecutionException, InterruptedException {
        String bingPushUrl = String.format(PUSH_ENDPOINT, settingProvider.getAccess());
        String pushBodyUrl = siteUrl + pageLink;
        log.info("Pushing to bing webmasters: {}", pushBodyUrl);
        BingPushBody bingPushBody = new BingPushBody();
        bingPushBody.setSiteUrl(siteUrl);
        bingPushBody.setUrlList(List.of(pushBodyUrl));
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        httpHeaders.add(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=utf-8");
        return httpRequestSender.request(bingPushUrl, HttpMethod.POST, httpHeaders,
            JsonUtils.objectToJson(bingPushBody));
    }

    @Data
    static
    class BingPushBody {
        private String siteUrl;
        private List<String> urlList;
    }
}
