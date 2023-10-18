package com.stonewu.sitepush.reconciler;

import com.stonewu.sitepush.DefaultSettingFetcher;
import com.stonewu.sitepush.service.PushService;
import com.stonewu.sitepush.setting.BasePushSetting;
import java.time.Duration;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;
import run.halo.app.core.extension.content.SinglePage;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.controller.Reconciler;

/**
 * @author Erzbir
 * @Date 2023/10/18
 */
@AllArgsConstructor
public abstract class AbstractPushReconciler implements Reconciler<Reconciler.Request> {
    protected final DefaultSettingFetcher settingFetcher;

    protected PushService pushService;

    protected final ExtensionClient client;

    protected Result getResult(Request request) {
        return client.fetch(SinglePage.class, request.name()).map(
                page -> {
                    BasePushSetting basePushSetting =
                        settingFetcher.fetch(BasePushSetting.CONFIG_MAP_NAME, BasePushSetting.GROUP,
                            BasePushSetting.class).orElseGet(BasePushSetting::new);
                    String siteUrl = basePushSetting.getSiteUrl();
                    if (basePushSetting.getEnable() && StringUtils.hasText(siteUrl)) {
                        if (page.isPublished()) {
                            String slug = page.getSpec().getSlug();
                            String permalink = page.getStatus().getPermalink();
                            boolean allPush =
                                pushService.isAllPush(siteUrl, page.getKind() + ":" + slug,
                                        permalink);
                            if (allPush) {
                                return Result.doNotRetry();
                            }
                        }
                        // 未完整推送完成时，10分钟后重试
                        return new Result(true, Duration.ofMinutes(10));
                    }
                    // 未启用插件时，忽略本次通知，10分钟后重试
                    return new Result(true, Duration.ofMinutes(10));
                })
            .orElseGet(() -> new Result(false, null));
    }
}
