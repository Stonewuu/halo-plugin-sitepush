package com.stonewu.sitepush.strategy;

import com.stonewu.sitepush.setting.BaiduPushSetting;
import com.stonewu.sitepush.setting.BaiduSettingProvider;
import com.stonewu.sitepush.setting.PushSettingProvider;
import com.stonewu.sitepush.utils.HttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.SettingFetcher;

@Component
@Slf4j
public class BaiduPushStrategy extends AbstractPushStrategy implements PushStrategy {
    public static final String PUSH_ENDPOINT = "http://data.zz.baidu.com/urls?site=%s&token=%s";

    public BaiduPushStrategy(SettingFetcher settingFetcher) {
        super(settingFetcher);
    }

    @Override
    public String getPushType() {
        return "baidu";
    }


    @Override
    protected PushSettingProvider getSettingProvider() {
        BaiduPushSetting baiduPushSetting =
            settingFetcher.fetch(BaiduPushSetting.GROUP,
                BaiduPushSetting.class).orElseGet(BaiduPushSetting::new);

        return new BaiduSettingProvider(baiduPushSetting);
    }

    @Override
    protected Mono<HttpResponse> request(PushSettingProvider settingProvider, String siteUrl,
        String... pageLinks)
        throws IOException, ExecutionException, InterruptedException {
        String baiduPushUrl = String.format(PUSH_ENDPOINT, siteUrl, settingProvider.getAccess());
        String[] pushBodyUrls = new String[pageLinks.length];
        for (int i = 0; i < pageLinks.length; i++) {
            pushBodyUrls[i] = siteUrl + pageLinks[i];
        }
        String pushUrls = Arrays.toString(pushBodyUrls);
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        httpHeaders.add(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        log.info("Pushing to baidu webmasters: {}", pushUrls);
        return httpRequestSender.request(baiduPushUrl, HttpMethod.POST,
            httpHeaders,
            pushUrls);
    }
}
