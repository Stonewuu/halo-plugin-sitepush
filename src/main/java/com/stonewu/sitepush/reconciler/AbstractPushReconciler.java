package com.stonewu.sitepush.reconciler;

import com.stonewu.sitepush.service.PushService;
import com.stonewu.sitepush.setting.BasePushSetting;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.extension.controller.Reconciler;
import run.halo.app.plugin.SettingFetcher;

/**
 * @author Erzbir
 * @Data: 2024/1/9 19:25
 */
@Slf4j
public abstract class AbstractPushReconciler implements Reconciler<Reconciler.Request> {
    protected SettingFetcher settingFetcher;
    protected ReactiveExtensionClient client;
    protected PushService pushService;

    private final AtomicInteger currentReGetTimes = new AtomicInteger(0);
    private static final int MAX_GET_TIMES = 10;
    private final AtomicBoolean isFirst = new AtomicBoolean(true);

    public AbstractPushReconciler(SettingFetcher settingFetcher, ReactiveExtensionClient client,
        PushService pushService) {
        this.settingFetcher = settingFetcher;
        this.client = client;
        this.pushService = pushService;
    }

    public interface PublishExtension {
        boolean isPublished();

        String getSlug();

        String getPermalink();

        String getKind();

        boolean isVisible();

        boolean isObserved();
    }

    protected Result reconcile(PublishExtension extension) {

        if (extension == null) {
            return Result.doNotRetry();
        }
        // 这里的写法为了保证引用的 SinglePage 或是其他 Extension 是完全初始化的
        if (extension.getPermalink() == null) {
            // 为了防止出现一直为 null 的情况
            if (currentReGetTimes.get() >= MAX_GET_TIMES) {
                return Result.doNotRetry();
            }
            currentReGetTimes.addAndGet(1);
            return new Result(true, Duration.ofMillis(100));
        }
        currentReGetTimes.set(0);

        BasePushSetting basePushSetting = getBasePushSetting();
        if (shouldRetry(basePushSetting, extension) && basePushSetting.getRetryInterval() != 0) {
            return new Result(true, Duration.ofMinutes(basePushSetting.getRetryInterval()));
        } else {
            return Result.doNotRetry();
        }
    }

    private BasePushSetting getBasePushSetting() {
        return settingFetcher.fetch(BasePushSetting.GROUP, BasePushSetting.class)
                .orElseGet(BasePushSetting::new);
    }

    private boolean shouldRetry(BasePushSetting basePushSetting,
                                PublishExtension publishExtension) {
        if (basePushSetting.getEnable() && StringUtils.hasText(basePushSetting.getSiteUrl())) {
            try {
                if (publishExtension.isPublished() && publishExtension.isObserved() && publishExtension.isVisible()) {
                    String slug = publishExtension.getSlug();
                    String permalink = publishExtension.getPermalink();
                    if (!checkIllegal(slug, permalink)) {
                        return true;
                    }
                    String siteUrl = basePushSetting.getSiteUrl().trim();
                    boolean allPush = pushService.pushUseAllStrategy(siteUrl,
                        publishExtension.getKind() + ":" + slug, permalink);
                    return !allPush;
                }
            } catch (Throwable e) {
                log.error(e.getMessage());
                return true;
            }
        }
        // 未启用不再重试，减少资源消耗
        return false;
    }

    private boolean checkIllegal(String key, String... pageLinks) {
        if (!StringUtils.hasText(key) || pageLinks == null || pageLinks.length == 0) {
            return false;
        }
        for (String pageLink : pageLinks) {
            if (!StringUtils.hasText(pageLink)) {
                return false;
            }
        }
        return true;
    }
}
