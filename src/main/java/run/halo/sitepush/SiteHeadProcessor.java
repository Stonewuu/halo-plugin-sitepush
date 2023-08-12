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


@Component
public class SiteHeadProcessor implements TemplateHeadProcessor {

    private final SettingFetcher settingFetcher;

    public SiteHeadProcessor(SettingFetcher settingFetcher) {
        this.settingFetcher = settingFetcher;
    }

    @Override
    public Mono<Void> process(ITemplateContext context, IModel model, IElementModelStructureHandler structureHandler) {
        return settingFetcher.fetch(BaiduPushSetting.GROUP, BaiduPushSetting.class)
            .map(config -> {
                final IModelFactory modelFactory = context.getModelFactory();
                model.add(modelFactory.createText(siteHead(config)));
                return Mono.empty();
            }).orElse(Mono.empty()).then();
    }

    private String siteHead(BaiduPushSetting config) {
        String script = "";

        //百度站长验证
        if (StringUtils.isNotBlank(config.getSiteVerification())){
            script = script + """
                              <meta name="baidu-site-verification" content="%s">
                """.formatted(config.getSiteVerification());
        }
        return script;
    }

    @Data
    public static class BasicConfig {
        String siteVerification;
    }
}