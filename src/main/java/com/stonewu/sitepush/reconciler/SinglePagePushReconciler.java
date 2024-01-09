package com.stonewu.sitepush.reconciler;

import com.stonewu.sitepush.DefaultSettingFetcher;
import com.stonewu.sitepush.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.core.extension.content.SinglePage;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.controller.Controller;
import run.halo.app.extension.controller.ControllerBuilder;
import run.halo.app.extension.controller.Reconciler;

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

    public SinglePagePushReconciler(DefaultSettingFetcher settingFetcher, ExtensionClient client,
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
    public Class<? extends AbstractExtension> getExtension() {
        return SinglePage.class;
    }
}
