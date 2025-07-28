package com.stonewu.sitepush;

import com.stonewu.sitepush.setting.BaiduPushSetting;
import com.stonewu.sitepush.setting.BaiduSettingProvider;
import com.stonewu.sitepush.setting.BingPushSetting;
import com.stonewu.sitepush.setting.BingPushSettingProvider;
import com.stonewu.sitepush.setting.GooglePushSetting;
import com.stonewu.sitepush.setting.GooglePushSettingProvider;
import com.stonewu.sitepush.setting.PushSettingProvider;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.ReactiveSettingFetcher;
import run.halo.app.theme.dialect.TemplateHeadProcessor;


@Component
public class SiteHeadProcessor implements TemplateHeadProcessor {

    private final ReactiveSettingFetcher settingFetcher;

    public SiteHeadProcessor(ReactiveSettingFetcher settingFetcher) {
        this.settingFetcher = settingFetcher;
    }

    public Mono<Void> process(ITemplateContext context, IModel model,
        IElementModelStructureHandler structureHandler) {
        final IModelFactory modelFactory = context.getModelFactory();

        Mono<PushSettingProvider> baiduProviderMono = settingFetcher
            .fetch(BaiduPushSetting.GROUP, BaiduPushSetting.class)
            .defaultIfEmpty(new BaiduPushSetting())
            .map(BaiduSettingProvider::new);

        Mono<PushSettingProvider> bingProviderMono = settingFetcher
            .fetch(BingPushSetting.GROUP, BingPushSetting.class)
            .defaultIfEmpty(new BingPushSetting())
            .map(BingPushSettingProvider::new);

        Mono<PushSettingProvider> googleProviderMono = settingFetcher
            .fetch(GooglePushSetting.GROUP, GooglePushSetting.class)
            .defaultIfEmpty(new GooglePushSetting())
            .map(GooglePushSettingProvider::new);

        return Flux.concat(baiduProviderMono, bingProviderMono, googleProviderMono)
            .filter(PushSettingProvider::isTagVerificationEnable)
            .doOnNext(
                provider -> model.add(modelFactory.createText(provider.getSiteVerificationMeta())))
            .then();
    }
}