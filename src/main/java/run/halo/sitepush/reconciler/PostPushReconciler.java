package run.halo.sitepush.reconciler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import run.halo.app.core.extension.content.Category;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.controller.Controller;
import run.halo.app.extension.controller.ControllerBuilder;
import run.halo.app.extension.controller.Reconciler;
import run.halo.sitepush.DefaultSettingFetcher;
import run.halo.sitepush.service.PushService;
import run.halo.sitepush.setting.PushBaseSetting;

/**
 * Reconciler for {@link Category}.
 *
 * @author guqing
 * @since 2.0.0
 */
@Component
@AllArgsConstructor
@Slf4j
public class PostPushReconciler implements Reconciler<Reconciler.Request> {

    private final DefaultSettingFetcher settingFetcher;

    private final ExtensionClient client;

    private PushService pushService;

    @Override
    public Result reconcile(Request request) {
        return client.fetch(Post.class, request.name())
            .map(post -> {
                PushBaseSetting pushBaseSetting =
                    settingFetcher.fetch(PushBaseSetting.CONFIG_MAP_NAME, PushBaseSetting.GROUP,
                        PushBaseSetting.class).orElseGet(PushBaseSetting::new);
                String siteUrl = pushBaseSetting.getSiteUrl();
                if (pushBaseSetting.getEnable() && StringUtils.hasText(siteUrl)) {
                    if (post.isPublished()) {
                        String slug = post.getSpec().getSlug();
                        String permalink = post.getStatus().getPermalink();
                        boolean allPush =
                            pushService.isAllPush(siteUrl, post.getKind() + ":" + slug,  permalink);
                    }
                }
                return Result.doNotRetry();
            })
            .orElseGet(() -> new Result(false, null));

    }

    @Override
    public Controller setupWith(ControllerBuilder builder) {
        return builder
            .extension(new Post())
            .build();
    }


}
