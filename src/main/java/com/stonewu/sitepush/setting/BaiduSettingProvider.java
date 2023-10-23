package com.stonewu.sitepush.setting;

import java.net.Proxy;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Erzbir
 * @Date 2023/10/17
 */
@AllArgsConstructor
public class BaiduSettingProvider implements PushSettingProvider {
    private BaiduPushSetting setting;

    @Override
    public String getConfigMapName() {
        return BaiduPushSetting.CONFIG_MAP_NAME;
    }

    @Override
    public String getGroup() {
        return BaiduPushSetting.GROUP;
    }

    @Override
    public Boolean isEnable() {
        return setting.getBaiduEnable();
    }

    @Override
    public Boolean isTagVerificationEnable() {
        return setting.getBaiduEnableTagVerification();
    }

    @Override
    public Class<?> getSettingClass() {
        return setting.getClass();
    }

    @Override
    public String getSiteVerification() {
        return setting.getSiteVerification();
    }

    @Override
    public String getAccess() {
        return setting.getToken();
    }

    @Override
    public String getSiteVerificationMeta() {
        String script = "";
        if (StringUtils.isNotBlank(getSiteVerification())) {
            script = script + """
                              <meta name="baidu-site-verification" content="%s" />
                """.formatted(getSiteVerification());
        }
        return script;
    }

    @Override
    public Boolean isUseProxy() {
        return setting.getBaiduProxyEnable();
    }

    @Override
    public Proxy.Type getProxyType() {
        return Proxy.Type.valueOf(setting.getBaiduProxyType());
    }

    @Override
    public String getProxyAddress() {
        return setting.getBaiduProxyAddress();
    }

    @Override
    public Integer getProxyPort() {
        return setting.getBaiduProxyPort();
    }

    @Override
    public Boolean proxyAuthEnable() {
        return setting.getBaiduProxyAuthEnable();
    }

    @Override
    public String getProxyUsername() {
        return setting.getBaiduProxyUsername();
    }

    @Override
    public String getProxyPassword() {
        return setting.getBaiduProxyPassword();
    }
}
