package com.stonewu.sitepush.strategy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.stonewu.sitepush.DefaultSettingFetcher;
import com.stonewu.sitepush.setting.BaiduPushSetting;
import com.stonewu.sitepush.setting.BaiduSettingProvider;
import com.stonewu.sitepush.setting.PushSettingProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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
    protected HttpResponse request(String siteUrl, String pageLink,
        PushSettingProvider settingProvider) {
        String baiduPushUrl = String.format(PUSH_ENDPOINT, siteUrl, settingProvider.getAccess());
        log.info("Pushing to baidu webmasters: {}", baiduPushUrl);
        HttpRequest request = HttpRequest.post(baiduPushUrl).body(siteUrl + pageLink);
        return httpRequestSender.request(request);
    }
}
