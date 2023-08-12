package run.halo.sitepush.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.core.extension.content.Category;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.Metadata;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.sitepush.DefaultSettingFetcher;
import run.halo.sitepush.GlobalCache;
import run.halo.sitepush.scheme.PushLog;
import run.halo.sitepush.scheme.PushUnique;
import run.halo.sitepush.strategy.PushStrategy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
@Slf4j
@AllArgsConstructor
public class PushServiceImpl implements PushService {

    private final DefaultSettingFetcher settingFetcher;

    private final ExtensionClient client;

    private Map<String, PushStrategy> pushStrategyMap;

    @Override
    public boolean isAllPush(String siteUrl, String slugKey, String permalink) {
        boolean allPush = true;
        for (Map.Entry<String, PushStrategy> entry : pushStrategyMap.entrySet()) {
            String key = entry.getKey();
            PushStrategy strategy = entry.getValue();
            String cacheKey = strategy.getPushType() + ":" + slugKey;

            PushUnique pushUnique = new PushUnique();
            pushUnique.setLastPushTime(Instant.now());
            pushUnique.setPushType(strategy.getPushType());
            pushUnique.setPushUniqueKey(slugKey);
            pushUnique.setPushStatus(1);
            // 没推送过或者推送过但是失败了的
            if (GlobalCache.PUSH_CACHE.get(cacheKey) == null || GlobalCache.PUSH_CACHE.get(cacheKey).getPushStatus() != 1) {
                GlobalCache.PUSH_CACHE.put(cacheKey, pushUnique);
                int pushStatus = strategy.push(siteUrl, cacheKey, permalink);
                pushUnique.setPushStatus(pushStatus);
                GlobalCache.PUSH_CACHE.put(cacheKey, pushUnique);
                Optional<PushUnique> fetch = client.fetch(PushUnique.class, pushUnique.getCacheKey());
                if (fetch.isPresent()) {
                    pushUnique = fetch.get();
                    pushUnique.setPushStatus(pushStatus);
                    client.update(pushUnique);
                } else {
                    Metadata metadata = new Metadata();
                    metadata.setName(cacheKey);
                    pushUnique.setMetadata(metadata);
                    client.create(pushUnique);
                }
                PushLog pushLog = new PushLog(Instant.now().getEpochSecond(), siteUrl + permalink, strategy.getPushType(), pushStatus);
                Metadata metadata = new Metadata();
                metadata.setName(UUID.randomUUID().toString());
                pushLog.setMetadata(metadata);
                if (pushStatus == 0) {
                    // 有一个失败了则返回false
                    allPush = false;
                    client.create(pushLog);
                }else if (pushStatus == 1){
                    client.create(pushLog);
                }
            }
        }
        return allPush;
    }
}
