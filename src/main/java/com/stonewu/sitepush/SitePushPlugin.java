package com.stonewu.sitepush;

import com.stonewu.sitepush.scheme.PushLog;
import com.stonewu.sitepush.scheme.PushUnique;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.Scheme;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.BasePlugin;

/**
 * 站点收录推送插件
 *
 * @author stonewu
 * @since 1.0.0
 */
@Component
@Slf4j
public class SitePushPlugin extends BasePlugin {
    private final SchemeManager schemeManager;
    private final ReactiveExtensionClient client;

    public SitePushPlugin(SchemeManager schemeManager,
        ReactiveExtensionClient client) {
        this.schemeManager = schemeManager;
        this.client = client;
    }

    @Override
    public void start() {
        registerScheme();
        loadPushedData();
    }

    @Override
    public void stop() {
        GlobalCache.PUSH_CACHE.clear();
        unregisterScheme();
    }

    private void registerScheme() {
        schemeManager.register(PushLog.class);
        schemeManager.register(PushUnique.class);
    }

    private void unregisterScheme() {
        Scheme pushLogScheme = schemeManager.get(PushLog.class);
        Scheme pushUniqueScheme = schemeManager.get(PushUnique.class);
        schemeManager.unregister(pushLogScheme);
        schemeManager.unregister(pushUniqueScheme);
    }

    private void loadPushedData() {
        Flux<PushUnique> fluxPushUnique = client.list(PushUnique.class, null, null);
        fluxPushUnique.doOnNext(pushUnique ->
                GlobalCache.PUSH_CACHE.put(pushUnique.getCacheKey(), pushUnique))
            .subscribe();
        log.info("成功读取已推送链接 {} 个", fluxPushUnique.count().block());
    }
}
