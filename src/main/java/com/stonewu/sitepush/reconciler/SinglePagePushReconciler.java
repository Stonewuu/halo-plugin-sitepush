package com.stonewu.sitepush.reconciler;

import com.stonewu.sitepush.DefaultSettingFetcher;
import com.stonewu.sitepush.setting.BasePushSetting;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import run.halo.app.core.extension.content.SinglePage;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.controller.Controller;
import run.halo.app.extension.controller.ControllerBuilder;
import run.halo.app.extension.controller.Reconciler;
import com.stonewu.sitepush.service.PushService;

import java.time.Duration;

/**
 * The {@link SinglePagePushReconciler} for route request to specific template <code>page
 * .html</code>.
 *
 * @author guqing
 * @since 2.0.0
 */
@Component
@AllArgsConstructor
@Slf4j
public class SinglePagePushReconciler implements Reconciler<Reconciler.Request> {

    private final DefaultSettingFetcher settingFetcher;

    private PushService pushService;

    private final ExtensionClient client;

    @Override
    public Result reconcile(Request request) {
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
                                            pushService.isAllPush(siteUrl, page.getKind() + ":" + slug, permalink);
                                    if(allPush){
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

    @Override
    public Controller setupWith(ControllerBuilder builder) {
        return builder
                .extension(new SinglePage())
                .build();
    }

}
