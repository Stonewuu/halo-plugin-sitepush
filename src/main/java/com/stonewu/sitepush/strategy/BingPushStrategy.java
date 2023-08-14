package com.stonewu.sitepush.strategy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.stonewu.sitepush.setting.BingPushSetting;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import run.halo.app.infra.utils.JsonUtils;
import com.stonewu.sitepush.DefaultSettingFetcher;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BingPushStrategy implements PushStrategy {
    private final DefaultSettingFetcher settingFetcher;

    @Override
    public String getPushType() {
        return "bing";
    }

    @Override
    public int push(String siteUrl, String key, String pageLink) {
        BingPushSetting bingPushSetting =
                settingFetcher.fetch(BingPushSetting.CONFIG_MAP_NAME, BingPushSetting.GROUP,
                        BingPushSetting.class).orElseGet(BingPushSetting::new);
        String apikey = bingPushSetting.getApikey();
        if (bingPushSetting.getBingEnable() && StringUtils.hasText(apikey)) {
            String bingPushUrl =
                    String.format("https://ssl.bing.com/webmaster/api.svc/json/SubmitUrlbatch?apikey=%s",
                            apikey);
            log.info("Pushing to bing webmasters: {}", bingPushUrl);
            BingPushBody bingPushBody = new BingPushBody();
            bingPushBody.setSiteUrl(siteUrl);
            bingPushBody.setUrlList(List.of(siteUrl + pageLink));
            HttpResponse execute = HttpRequest.post(bingPushUrl)
                    .body(JsonUtils.objectToJson(bingPushBody))
                    .contentType("application/json; charset=utf-8")
                    .execute();
            log.info("Pushing to bing webmasters Result: {}", execute.body());
            boolean ok = execute.isOk();
            return ok ? 1 : 0;
        }
        return -1;
    }

    @Data
    class BingPushBody {
        private String siteUrl;
        private List<String> urlList;
    }
}
