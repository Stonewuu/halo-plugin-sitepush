package run.halo.sitepush.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.sitepush.DefaultSettingFetcher;
import run.halo.sitepush.GlobalCache;
import run.halo.sitepush.strategy.PushStrategy;

import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
public class PushServiceImpl implements PushService {

    private final DefaultSettingFetcher settingFetcher;

    private final ExternalUrlSupplier externalUrlSupplier;

    private Map<String, PushStrategy> pushStrategyMap;

    @Override
    public boolean isAllPush(String siteUrl, String slugKey, String permalink) {
        boolean allPush = true;
        for (Map.Entry<String, PushStrategy> entry : pushStrategyMap.entrySet()) {
            String key = entry.getKey();
            PushStrategy strategy = entry.getValue();
            String cacheKey = strategy.getPushType() + ":" + slugKey;
            if (GlobalCache.PUSH_CACHE.get(cacheKey) == null) {
                GlobalCache.PUSH_CACHE.put(cacheKey, true);
                boolean success = strategy.push(siteUrl, cacheKey, permalink);
                if (!success) {
                    GlobalCache.PUSH_CACHE.remove(cacheKey);
                    allPush = success;
                }
            }
        }
        return allPush;
    }
}
