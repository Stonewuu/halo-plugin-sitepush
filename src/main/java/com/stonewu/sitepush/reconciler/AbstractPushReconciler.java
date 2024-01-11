package com.stonewu.sitepush.reconciler;

import com.stonewu.sitepush.service.PushService;
import com.stonewu.sitepush.setting.BasePushSetting;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import run.halo.app.core.extension.content.Post;
import run.halo.app.core.extension.content.SinglePage;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.controller.Reconciler;
import run.halo.app.plugin.SettingFetcher;

/**
 * @author Erzbir
 * @Data: 2024/1/9 19:25
 */
@Slf4j
public abstract class AbstractPushReconciler implements Reconciler<Reconciler.Request> {
    protected SettingFetcher settingFetcher;

    protected ExtensionClient client;

    protected PushService pushService;

    public AbstractPushReconciler(SettingFetcher settingFetcher, ExtensionClient client,
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
    }

    public record PostAdapter(Post post) implements PublishExtension {

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

    public record SinglePageAdapter(SinglePage singlePage) implements PublishExtension {

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

    protected Result reconcile(PublishExtension extension) {
        if (extension == null) {
            return new Result(false, null);
        }
        BasePushSetting basePushSetting = getBasePushSetting();
        if (shouldRetry(basePushSetting, extension)) {
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
                if (publishExtension.isPublished()) {
                    String slug = publishExtension.getSlug();
                    String permalink = publishExtension.getPermalink();
                    if (!checkIllegal(slug, permalink)) {
                        return true;
                    }
                    boolean allPush = pushService.pushUseAllStrategy(basePushSetting.getSiteUrl(),
                        publishExtension.getKind() + ":" + slug, permalink);
                    return !allPush;
                }
            } catch (Throwable e) {
                log.error(e.getMessage());
                return true;
            }
        }
        return true;
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
