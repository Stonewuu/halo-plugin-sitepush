package com.stonewu.sitepush.setting;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Erzbir
 * @Date 2023/10/17
 */
@AllArgsConstructor
public class BingPushSettingProvider implements PushSettingProvider {
    private BingPushSetting bingPushSetting;

    @Override
    public String getConfigMapName() {
        return BingPushSetting.CONFIG_MAP_NAME;
    }

    @Override
    public String getGroup() {
        return BingPushSetting.GROUP;
    }

    @Override
    public Boolean isEnable() {
        return bingPushSetting.getBingEnable();
    }

    @Override
    public Boolean isDomainVerification() {
        return bingPushSetting.getIsBingDomainVerification();
    }

    @Override
    public Class<?> getSettingClass() {
        return bingPushSetting.getClass();
    }

    @Override
    public String getSiteVerification() {
        return bingPushSetting.getBingSiteVerification();
    }

    @Override
    public String getAccess() {
        return bingPushSetting.getApikey();
    }

    @Override
    public String getSiteVerificationMeta() {
        String script = "";

        if (StringUtils.isNotBlank(getSiteVerification())) {
            script = script + """
                              <meta name="msvalidate.01" content="%s">
                """.formatted(getSiteVerification());
        }
        return script;
    }
}
