package com.stonewu.sitepush;

import com.stonewu.sitepush.setting.BaiduPushSetting;
import com.stonewu.sitepush.setting.BaiduSettingProvider;
import com.stonewu.sitepush.setting.BingPushSetting;
import com.stonewu.sitepush.setting.BingPushSettingProvider;
import com.stonewu.sitepush.setting.GooglePushSetting;
import com.stonewu.sitepush.setting.GooglePushSettingProvider;
import com.stonewu.sitepush.setting.PushSettingProvider;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.SettingFetcher;
import run.halo.app.theme.dialect.TemplateHeadProcessor;


@Component
public class SiteHeadProcessor implements TemplateHeadProcessor {

    private final SettingFetcher settingFetcher;
    private final List<PushSettingProvider> settingProviders;

    public SiteHeadProcessor(SettingFetcher settingFetcher) {
        this.settingFetcher = settingFetcher;
        settingProviders = Arrays.asList(
            new BaiduSettingProvider(
                settingFetcher.fetch(BaiduPushSetting.GROUP, BaiduPushSetting.class)
                    .orElse(new BaiduPushSetting())),
            new BingPushSettingProvider(
                settingFetcher.fetch(BingPushSetting.GROUP, BingPushSetting.class)
                    .orElse(new BingPushSetting())),
            new GooglePushSettingProvider(
                settingFetcher.fetch(GooglePushSetting.GROUP, GooglePushSetting.class)
                    .orElse(new GooglePushSetting()))
        );
    }

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model,
        IElementModelStructureHandler structureHandler) {
        final IModelFactory modelFactory = context.getModelFactory();
        Flux<?> fetchAndAddHeaders = Flux.fromIterable(settingProviders)
            .flatMap(
                provider -> {
                    if (provider.isTagVerificationEnable()) {
                        return settingFetcher.fetch(provider.getGroup(), provider.getSettingClass())
                            .map(config -> {
                                model.add(
                                    modelFactory.createText(
                                        provider.getSiteVerificationMeta()));
                                return Mono.empty();
                            }).orElse(Mono.empty());
                    }
                    return Mono.empty();
                }
            );
        return Mono.when(fetchAndAddHeaders).then();
    }

    @Data
    public static class BasicConfig {
        String siteVerification;
    }
}