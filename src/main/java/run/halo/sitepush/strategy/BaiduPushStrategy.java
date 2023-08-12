package run.halo.sitepush.strategy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import run.halo.sitepush.GlobalCache;
import run.halo.sitepush.DefaultSettingFetcher;
import run.halo.sitepush.setting.PushBaiduSetting;

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
    public boolean push(String key, String pageLink) {
        PushBaiduSetting pushBaiduSetting =
            settingFetcher.fetch(PushBaiduSetting.CONFIG_MAP_NAME, PushBaiduSetting.GROUP,
                PushBaiduSetting.class).orElseGet(() -> new PushBaiduSetting());
        String token = pushBaiduSetting.getToken();
        if (pushBaiduSetting.getBaiduEnable() && StringUtils.hasText(token)) {
            if (GlobalCache.PUSH_CACHE.get(key) == null) {
                GlobalCache.PUSH_CACHE.put(key, true);
                String baiduPushUrl =
                    String.format("http://data.zz.baidu.com/urls?site=%s&token=%s",
                        pageLink, token);
                log.info("Pushing to baidu: {}", baiduPushUrl);
                HttpResponse execute = HttpRequest.post(baiduPushUrl).form("site", pageLink).execute();
                log.info("Pushing to baidu Result: {}", execute.body());
                boolean ok = execute.isOk();
                GlobalCache.PUSH_CACHE.remove(key);
                return ok;
            }
        }
        return true;
    }
}
