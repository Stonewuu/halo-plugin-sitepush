package com.stonewu.sitepush.strategy;

import com.stonewu.sitepush.DefaultSettingFetcher;
import com.stonewu.sitepush.setting.BaiduPushSetting;
import com.stonewu.sitepush.setting.BaiduSettingProvider;
import com.stonewu.sitepush.setting.PushSettingProvider;
import com.stonewu.sitepush.utils.HttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class BaiduPushStrategy extends AbstractPushStrategy implements PushStrategy {
    public static final String PUSH_ENDPOINT = "http://data.zz.baidu.com/urls?site=%s&token=%s";

    public BaiduPushStrategy(DefaultSettingFetcher settingFetcher) {
        super(settingFetcher);
    }

    @Override
    public String getPushType() {
        return "baidu";
    }


    @Override
    protected PushSettingProvider getSettingProvider() {
        BaiduPushSetting baiduPushSetting =
            settingFetcher.fetch(BaiduPushSetting.CONFIG_MAP_NAME, BaiduPushSetting.GROUP,
                BaiduPushSetting.class).orElseGet(BaiduPushSetting::new);

        return new BaiduSettingProvider(baiduPushSetting);
    }

    @Override
    protected Mono<HttpResponse> request(String siteUrl, String pageLink,
        PushSettingProvider settingProvider)
        throws IOException, ExecutionException, InterruptedException {
        String baiduPushUrl = String.format(PUSH_ENDPOINT, siteUrl, settingProvider.getAccess());
        String pushBodyUrl = siteUrl + pageLink;
        log.info("Pushing to baidu webmasters: {}", pushBodyUrl);
        return httpRequestSender.request(baiduPushUrl, HttpMethod.POST,
            new DefaultHttpHeaders(),
            pushBodyUrl);
    }
}
