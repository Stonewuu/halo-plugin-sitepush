package com.stonewu.sitepush.strategy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.stonewu.sitepush.DefaultSettingFetcher;
import com.stonewu.sitepush.setting.BingPushSetting;
import com.stonewu.sitepush.setting.BingPushSettingProvider;
import com.stonewu.sitepush.setting.PushSettingProvider;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
    protected HttpResponse request(String siteUrl, String pageLink,
        PushSettingProvider settingProvider) {
        String bingPushUrl = String.format(PUSH_ENDPOINT, settingProvider.getAccess());
        log.info("Pushing to bing webmasters: {}", bingPushUrl);
        BingPushBody bingPushBody = new BingPushBody();
        bingPushBody.setSiteUrl(siteUrl);
        bingPushBody.setUrlList(List.of(siteUrl + pageLink));
        HttpRequest httpRequest = HttpRequest.post(bingPushUrl)
            .body(JsonUtils.objectToJson(bingPushBody))
            .contentType("application/json; charset=utf-8");
        return httpRequestSender.request(httpRequest);
    }

    @Data
    class BingPushBody {
        private String siteUrl;
        private List<String> urlList;
    }
}
