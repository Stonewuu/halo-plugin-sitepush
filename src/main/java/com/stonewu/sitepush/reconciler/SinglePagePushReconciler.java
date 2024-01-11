package com.stonewu.sitepush.reconciler;

import com.stonewu.sitepush.service.PushService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.core.extension.content.SinglePage;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.controller.Controller;
import run.halo.app.extension.controller.ControllerBuilder;
import run.halo.app.extension.controller.Reconciler;
import run.halo.app.plugin.SettingFetcher;

/**
 * The {@link SinglePagePushReconciler} for route request to specific template <code>page
 * .html</code>.
 *
 * @author guqing
 * @since 2.0.0
 */
@Component
@Slf4j
public class SinglePagePushReconciler extends AbstractPushReconciler
    implements Reconciler<Reconciler.Request> {

    public SinglePagePushReconciler(SettingFetcher settingFetcher, ExtensionClient client,
        PushService pushService) {
        super(settingFetcher, client, pushService);
    }

    @Override
    public Controller setupWith(ControllerBuilder builder) {
        return builder
            .extension(new SinglePage())
            .build();
    }

    @Override
    public Result reconcile(Request request) {
        Optional<PublishExtension> publishExtension = client.fetch(SinglePage.class, request.name())
            .map(SinglePageAdapter::new);
        return reconcile(publishExtension.get());
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
}
