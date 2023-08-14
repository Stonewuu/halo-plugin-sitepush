package com.stonewu.sitepush;

import lombok.extern.slf4j.Slf4j;
import org.pf4j.PluginWrapper;
import org.springframework.stereotype.Component;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.Scheme;
import run.halo.app.extension.SchemeManager;
import run.halo.app.plugin.BasePlugin;
import com.stonewu.sitepush.scheme.PushLog;
import com.stonewu.sitepush.scheme.PushUnique;

import java.util.HashMap;
import java.util.List;

/**
 * <p>Plugin main class to manage the lifecycle of the plugin.</p>
 * <p>This class must be public and have a public constructor.</p>
 * <p>Only one main class extending {@link BasePlugin} is allowed per plugin.</p>
 *
 * @author guqing
 * @since 1.0.0
 */
@Component
@Slf4j
public class SitePushPlugin extends BasePlugin {
    String pathPrefix = "/root/.halo2/";
    private final SchemeManager schemeManager;

    private final ExtensionClient client;

    public SitePushPlugin(PluginWrapper wrapper, SchemeManager schemeManager, ExtensionClient client) {
        super(wrapper);
        this.schemeManager = schemeManager;
        this.client = client;
    }

    @Override
    public void start() {
        schemeManager.register(PushLog.class);
        schemeManager.register(PushUnique.class);
        List<PushUnique> list = client.list(PushUnique.class, null, null);
        list.forEach(pushUnique -> {
            log.info("init---初始化---------{}----|{}", pushUnique.getCacheKey(), pushUnique.getPushStatus());
            GlobalCache.PUSH_CACHE.put(pushUnique.getCacheKey(), pushUnique);
        });
    }

    @Override
    public void stop() {
        List<PushLog> list = client.list(PushLog.class, null, null);
        list.forEach(pushLog -> {
            client.delete(pushLog);
        });
        GlobalCache.PUSH_CACHE = new HashMap<>();
        Scheme pushLogScheme = schemeManager.get(PushLog.class);
        Scheme pushUniqueScheme = schemeManager.get(PushUnique.class);
        schemeManager.unregister(pushLogScheme);
        schemeManager.unregister(pushUniqueScheme);
    }
}
