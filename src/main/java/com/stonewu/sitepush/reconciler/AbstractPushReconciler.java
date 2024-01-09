package com.stonewu.sitepush.reconciler;

import com.stonewu.sitepush.DefaultSettingFetcher;
import com.stonewu.sitepush.service.PushService;
import com.stonewu.sitepush.setting.BasePushSetting;
import java.time.Duration;
import org.springframework.util.StringUtils;
import run.halo.app.core.extension.content.Post;
import run.halo.app.core.extension.content.SinglePage;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.controller.Reconciler;

/**
 * @author Erzbir
 * @Data: 2024/1/9 19:25
 */
public abstract class AbstractPushReconciler implements Reconciler<Reconciler.Request> {
    protected DefaultSettingFetcher settingFetcher;

    protected ExtensionClient client;

    protected PushService pushService;

    public AbstractPushReconciler(DefaultSettingFetcher settingFetcher, ExtensionClient client,
        PushService pushService) {
        this.settingFetcher = settingFetcher;
        this.client = client;
        this.pushService = pushService;
    }

    private interface PublishExtension {
        boolean isPublished();

        String getSlug();

        String getPermalink();

        String getKind();
    }

    private record PostAdapter(Post post) implements PublishExtension {

        @Override
        public boolean isPublished() {
            return post.isPublished();
        }

        @Override
        public String getSlug() {
            return post.getSpec().getSlug();
        }

        @Override
        public String getPermalink() {
            return post.getStatus().getPermalink();
        }

        @Override
        public String getKind() {
            return post.getKind();
        }
    }

    private record SinglePageAdapter(SinglePage singlePage) implements PublishExtension {

        @Override
        public boolean isPublished() {
            return singlePage.isPublished();
        }

        @Override
        public String getSlug() {
            return singlePage.getSpec().getSlug();
        }

        @Override
        public String getPermalink() {
            return singlePage.getStatus().getPermalink();
        }

        @Override
        public String getKind() {
            return singlePage.getKind();
        }
    }

    @Override
    public Result reconcile(Request request) {
        return client.fetch(getExtension(), request.name())
            .map(extension -> {
                PublishExtension publishExtension;
                if (extension instanceof Post post) {
                    publishExtension = new PostAdapter(post);
                } else if (extension instanceof SinglePage singlePage) {
                    publishExtension = new SinglePageAdapter(singlePage);
                } else {
                    throw new RuntimeException("不支持此类型");
                }
                BasePushSetting basePushSetting =
                    settingFetcher.fetch(BasePushSetting.CONFIG_MAP_NAME, BasePushSetting.GROUP,
                        BasePushSetting.class).orElseGet(BasePushSetting::new);
                int retryInterval = basePushSetting.getRetryInterval();
                String siteUrl = basePushSetting.getSiteUrl();
                if (basePushSetting.getEnable() && StringUtils.hasText(siteUrl)) {
                    if (publishExtension.isPublished()) {
                        String slug = publishExtension.getSlug();
                        String permalink = publishExtension.getPermalink();
                        boolean allPush =
                            pushService.isAllPush(siteUrl, publishExtension.getKind() + ":" + slug,
                                permalink);
                        if (allPush) {
                            return Result.doNotRetry();
                        }
                    }
                    // 未完整推送完成时，10分钟后重试
                    return new Result(true, Duration.ofMinutes(retryInterval));
                }
                // 未启用插件时，忽略本次通知，10分钟后重试
                return new Result(true, Duration.ofMinutes(retryInterval));
            })
            .orElseGet(() -> new Result(false, null));
    }

    public abstract Class<? extends AbstractExtension> getExtension();
}
