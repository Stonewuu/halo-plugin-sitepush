package com.stonewu.sitepush.setting;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Erzbir
 * @Date 2023/10/17
 */
@AllArgsConstructor
public class GooglePushSettingProvider implements PushSettingProvider {
    private GooglePushSetting googlePushSetting;

    @Override
    public String getConfigMapName() {
        return GooglePushSetting.CONFIG_MAP_NAME;
    }

    @Override
    public String getGroup() {
        return GooglePushSetting.GROUP;
    }

    @Override
    public Boolean isEnable() {
        return googlePushSetting.getGoogleEnable();
    }

    @Override
    public Boolean isTagVerificationEnable() {
        return googlePushSetting.getGoogleEnableTagVerification();
    }

    @Override
    public Class<?> getSettingClass() {
        return googlePushSetting.getClass();
    }

    @Override
    public String getSiteVerification() {
        return googlePushSetting.getGoogleSiteVerification();
    }

    @Override
    public String getAccess() {
        return googlePushSetting.getCredentialsJson();
    }

    @Override
    public String getSiteVerificationMeta() {
        String script = "";

        if (StringUtils.isNotBlank(getSiteVerification())) {
            script = script + """
                              <meta name="google-site-verification" content="%s" />
                """.formatted(getSiteVerification());
        }
        return script;
    }
}
