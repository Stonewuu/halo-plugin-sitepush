package com.stonewu.sitepush.setting;

import java.net.Proxy;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Erzbir
 * @Date 2023/10/17
 */
@AllArgsConstructor
public class GooglePushSettingProvider implements PushSettingProvider {
    private GooglePushSetting setting;

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
        return setting.getGoogleEnable();
    }

    @Override
    public Boolean isTagVerificationEnable() {
        return setting.getGoogleEnableTagVerification();
    }

    @Override
    public Class<?> getSettingClass() {
        return setting.getClass();
    }

    @Override
    public String getSiteVerification() {
        return setting.getGoogleSiteVerification();
    }

    @Override
    public String getAccess() {
        return setting.getCredentialsJson();
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

    @Override
    public Boolean isUseProxy() {
        return setting.getGoogleProxyEnable();
    }

    @Override
    public Proxy.Type getProxyType() {
        return Proxy.Type.valueOf(setting.getGoogleProxyType());
    }

    @Override
    public String getProxyAddress() {
        return setting.getGoogleProxyAddress();
    }

    @Override
    public Integer getProxyPort() {
        return setting.getGoogleProxyPort();
    }

    @Override
    public Boolean proxyAuthEnable() {
        return setting.getGoogleProxyAuthEnable();
    }

    @Override
    public String getProxyUsername() {
        return setting.getGoogleProxyUsername();
    }

    @Override
    public String getProxyPassword() {
        return setting.getGoogleProxyPassword();
    }
}
