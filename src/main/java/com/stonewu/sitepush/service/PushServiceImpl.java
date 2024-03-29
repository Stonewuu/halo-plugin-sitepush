package com.stonewu.sitepush.service;

import com.stonewu.sitepush.GlobalCache;
import com.stonewu.sitepush.scheme.PushUnique;
import com.stonewu.sitepush.strategy.PushStrategy;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;

@Component
@Slf4j
@AllArgsConstructor
public class PushServiceImpl implements PushService {
    private final ReactiveExtensionClient client;

    private Map<String, PushStrategy> pushStrategyMap;

    @Override
    public boolean pushUseAllStrategy(String siteUrl, String slugKey, String... permalinks) {
        boolean allPush = true;
        for (Map.Entry<String, PushStrategy> entry : pushStrategyMap.entrySet()) {
            allPush = push(siteUrl, entry.getValue(), slugKey, permalinks);
        }
        return allPush;
    }


    @Override
    public boolean push(String siteUrl, PushStrategy pushStrategy, String slugKey,
        String... permalinks) {
        String cacheKey = pushStrategy.getPushType() + ":" + slugKey;
        int status = 0;
        for (String permalink : permalinks) {
            // 没推送过或者推送过但是失败了的
            if (isNeedPush(cacheKey)) {
                status = pushStrategy.push(siteUrl, slugKey, permalink);
                recordPushResult(pushStrategy.getPushType(), slugKey, cacheKey, status);
            }
        }
        return status == 1;
    }

    public void recordPushResult(String pushType, String slugKey, String cacheKey, int status) {
        PushUnique pushUnique = new PushUnique();
        pushUnique.setLastPushTime(Instant.now());
        pushUnique.setPushType(pushType);
        pushUnique.setPushUniqueKey(slugKey);
        pushUnique.setPushStatus(status);
        GlobalCache.PUSH_CACHE.put(cacheKey, pushUnique);
        Optional<PushUnique> fetch =
            client.fetch(PushUnique.class, pushUnique.getCacheKey()).blockOptional();
        if (fetch.isPresent()) {
            pushUnique = fetch.get();
            client.update(pushUnique).subscribe();
        } else {
            Metadata metadata = new Metadata();
            metadata.setName(cacheKey);
            pushUnique.setMetadata(metadata);
            client.create(pushUnique).subscribe();
        }
    }

    public boolean isNeedPush(String cacheKey) {
        return GlobalCache.PUSH_CACHE.get(cacheKey) == null
            || GlobalCache.PUSH_CACHE.get(cacheKey).getPushStatus() != 1;
    }
}
