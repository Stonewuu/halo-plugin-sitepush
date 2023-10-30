package com.stonewu.sitepush.setting;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import reactor.netty.transport.ProxyProvider;

/**
 * @author Erzbir
 * @Date 2023/10/17
 */
@AllArgsConstructor
public class BingPushSettingProvider implements PushSettingProvider {
    private BingPushSetting setting;

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
        return setting.getBingEnable();
    }

    @Override
    public Boolean isTagVerificationEnable() {
        return setting.getBingEnableTagVerification();
    }

    @Override
    public Class<?> getSettingClass() {
        return setting.getClass();
    }

    @Override
    public String getSiteVerification() {
        return setting.getBingSiteVerification();
    }

    @Override
    public String getAccess() {
        return setting.getApikey();
    }

    @Override
    public String getSiteVerificationMeta() {
        String script = "";

        if (StringUtils.isNotBlank(getSiteVerification())) {
            script = script + """
                              <meta name="msvalidate.01" content="%s" />
                """.formatted(getSiteVerification());
        }
        return script;
    }

    @Override
    public Boolean isUseProxy() {
        return setting.getBingProxyEnable();
    }

    @Override
    public ProxyProvider.Proxy getProxyType() {
        if (setting.getBingProxyType().equals("SOCKS")) {
            return ProxyProvider.Proxy.SOCKS5;
        }
        return ProxyProvider.Proxy.valueOf(setting.getBingProxyType());
    }

    @Override
    public String getProxyAddress() {
        return setting.getBingProxyAddress();
    }

    @Override
    public Integer getProxyPort() {
        return setting.getBingProxyPort();
    }

    @Override
    public Boolean proxyAuthEnable() {
        return setting.getBingProxyAuthEnable();
    }

    @Override
    public String getProxyUsername() {
        return setting.getBingProxyUsername();
    }

    @Override
    public String getProxyPassword() {
        return setting.getBingProxyPassword();
    }
}
