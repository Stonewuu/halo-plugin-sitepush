package com.stonewu.sitepush.reconciler;

import com.stonewu.sitepush.DefaultSettingFetcher;
import com.stonewu.sitepush.service.PushService;
import com.stonewu.sitepush.setting.BasePushSetting;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import run.halo.app.core.extension.content.Category;
import run.halo.app.core.extension.content.Post;
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


    public PostPushReconciler(DefaultSettingFetcher settingFetcher, PushService pushService,
        ExtensionClient client) {
        super(settingFetcher, pushService, client);
    }

    @Override
    public Result reconcile(Request request) {
        return getResult(request);

    }

    @Override
    public Controller setupWith(ControllerBuilder builder) {
        return builder
            .extension(new Post())
            .build();
    }


}
