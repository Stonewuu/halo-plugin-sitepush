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

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

/**
 * 站点收录推送插件
 * @author stonewu
 * @since 1.0.0
 */
@Component
@Slf4j
public class SitePushPlugin extends BasePlugin {
    String pathPrefix = "/root/.halo2/";
    private final SchemeManager schemeManager;

    private final ExtensionClient client;

    public SitePushPlugin(PluginWrapper wrapper, SchemeManager schemeManager,
        ExtensionClient client) {
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
            GlobalCache.PUSH_CACHE.put(pushUnique.getCacheKey(), pushUnique);
        });
        List<PushLog> logList = client.list(PushLog.class, null, null);
        logList.forEach(pushLog -> {
            long epochSecond = Instant.now().getEpochSecond();
            // 每次启动插件删除一个月前的推送日志
            if (pushLog.getCreateTime() + 60 * 24 * 30 < epochSecond) {
                client.delete(pushLog);
            }
        });
    }

    @Override
    public void stop() {
        GlobalCache.PUSH_CACHE = new HashMap<>();
        Scheme pushLogScheme = schemeManager.get(PushLog.class);
        Scheme pushUniqueScheme = schemeManager.get(PushUnique.class);
        schemeManager.unregister(pushLogScheme);
        schemeManager.unregister(pushUniqueScheme);
    }
}
