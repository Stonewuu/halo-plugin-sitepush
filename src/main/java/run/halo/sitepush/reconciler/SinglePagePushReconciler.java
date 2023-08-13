package run.halo.sitepush.reconciler;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import run.halo.app.core.extension.content.SinglePage;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.controller.Controller;
import run.halo.app.extension.controller.ControllerBuilder;
import run.halo.app.extension.controller.Reconciler;
import run.halo.sitepush.DefaultSettingFetcher;
import run.halo.sitepush.service.PushService;
import run.halo.sitepush.setting.PushBaseSetting;

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
                            PushBaseSetting pushBaseSetting =
                                    settingFetcher.fetch(PushBaseSetting.CONFIG_MAP_NAME, PushBaseSetting.GROUP,
                                            PushBaseSetting.class).orElseGet(PushBaseSetting::new);
                            String siteUrl = pushBaseSetting.getSiteUrl();
                            if (pushBaseSetting.getEnable() && StringUtils.hasText(siteUrl)) {
                                if (page.isPublished()) {
                                    String slug = page.getSpec().getSlug();
                                    String permalink = page.getStatus().getPermalink();
                                    boolean allPush =
                                            pushService.isAllPush(siteUrl, page.getKind() + ":" + slug, permalink);
                                }
                            }
                            return Result.doNotRetry();
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
