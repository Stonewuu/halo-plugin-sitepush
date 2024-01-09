package com.stonewu.sitepush.reconciler;

import com.stonewu.sitepush.DefaultSettingFetcher;
import com.stonewu.sitepush.service.PushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.core.extension.content.Category;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.AbstractExtension;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.controller.Controller;
import run.halo.app.extension.controller.ControllerBuilder;
import run.halo.app.extension.controller.Reconciler;

/**
 * Reconciler for {@link Category}.
 *
 * @author guqing
 * @since 2.0.0
 */
@Component
@Slf4j
public class PostPushReconciler extends AbstractPushReconciler
    implements Reconciler<Reconciler.Request> {

    public PostPushReconciler(DefaultSettingFetcher settingFetcher, ExtensionClient client,
        PushService pushService) {
        super(settingFetcher, client, pushService);
    }

    @Override
    public Class<? extends AbstractExtension> getExtension() {
        return Post.class;
    }

    @Override
    public Controller setupWith(ControllerBuilder builder) {
        return builder
            .extension(new Post())
            .build();
    }
}
