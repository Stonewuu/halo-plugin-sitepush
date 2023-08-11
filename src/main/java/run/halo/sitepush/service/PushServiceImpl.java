package run.halo.sitepush.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.infra.ExternalUrlSupplier;
import run.halo.sitepush.GlobalCache;
import run.halo.sitepush.DefaultSettingFetcher;
import run.halo.sitepush.strategy.PushStrategy;
import java.util.Map;

@Component
@Slf4j
@AllArgsConstructor
public class PushServiceImpl implements PushService{

    private final DefaultSettingFetcher settingFetcher;

    private final ExternalUrlSupplier externalUrlSupplier;

    private Map<String, PushStrategy> pushStrategyMap;

    @Override
    public boolean isAllPush(String slugKey, String permalink) {
        boolean allPush = true;
        for (Map.Entry<String, PushStrategy> entry : pushStrategyMap.entrySet()) {
            String key = entry.getKey();
            PushStrategy strategy = entry.getValue();

            boolean pushed = strategy.push(slugKey, permalink);
            if (!pushed) {
                allPush = pushed;
            }
        }
        return allPush;
    }
}
