package run.halo.sitepush.strategy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import run.halo.sitepush.DefaultSettingFetcher;
import run.halo.sitepush.setting.BaiduPushSetting;

@Component
@AllArgsConstructor
@Slf4j
public class BaiduPushStrategy implements PushStrategy {
    private final DefaultSettingFetcher settingFetcher;

    @Override
    public String getPushType() {
        return "baidu";
    }

    @Override
    public boolean push(String siteUrl, String key, String pageLink) {
        BaiduPushSetting baiduPushSetting =
            settingFetcher.fetch(BaiduPushSetting.CONFIG_MAP_NAME, BaiduPushSetting.GROUP,
                BaiduPushSetting.class).orElseGet(() -> new BaiduPushSetting());
        String token = baiduPushSetting.getToken();
        if (baiduPushSetting.getBaiduEnable() && StringUtils.hasText(token)) {
            String baiduPushUrl =
                String.format("http://data.zz.baidu.com/urls?site=%s&token=%s",
                    siteUrl, token);
            log.info("Pushing to baidu: {}", baiduPushUrl);
            HttpResponse execute = HttpRequest.post(baiduPushUrl).body(siteUrl + pageLink).execute();
            log.info("Pushing to baidu Result: {}", execute.body());
            boolean ok = execute.isOk();
            return ok;
        }
        return true;
    }
}
