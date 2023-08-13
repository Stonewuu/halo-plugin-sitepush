package run.halo.sitepush;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.processor.element.IElementModelStructureHandler;
import reactor.core.publisher.Mono;
import run.halo.app.plugin.SettingFetcher;
import run.halo.app.theme.dialect.TemplateHeadProcessor;
import run.halo.sitepush.setting.BaiduPushSetting;
import run.halo.sitepush.setting.BingPushSetting;


@Component
public class SiteHeadProcessor implements TemplateHeadProcessor {

    private final SettingFetcher settingFetcher;

    public SiteHeadProcessor(SettingFetcher settingFetcher) {
        this.settingFetcher = settingFetcher;
    }

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model, IElementModelStructureHandler structureHandler) {
        final IModelFactory modelFactory = context.getModelFactory();
        Mono<Void> baidu = settingFetcher.fetch(BaiduPushSetting.GROUP, BaiduPushSetting.class)
                .map(config -> {
                    model.add(modelFactory.createText(baiduSiteHead(config)));
                    return Mono.empty();
                }).orElse(Mono.empty()).then(settingFetcher.fetch(BingPushSetting.GROUP, BingPushSetting.class)
                        .map(config -> {
                            model.add(modelFactory.createText(bingSiteHead(config)));
                            return Mono.empty();
                        }).orElse(Mono.empty()).then());

        return baidu;
    }

    private String baiduSiteHead(BaiduPushSetting config) {
        String script = "";

        //百度站长验证
        if (StringUtils.isNotBlank(config.getSiteVerification())) {
            script = script + """
                                  <meta name="baidu-site-verification" content="%s">
                    """.formatted(config.getSiteVerification());
        }
        return script;
    }

    private String bingSiteHead(BingPushSetting config) {
        String script = "";

        //百度站长验证
        if (StringUtils.isNotBlank(config.getBingSiteVerification())) {
            script = script + """
                                  <meta name="msvalidate.01" content="%s">
                    """.formatted(config.getBingSiteVerification());
        }
        return script;
    }

    @Data
    public static class BasicConfig {
        String siteVerification;
    }
}