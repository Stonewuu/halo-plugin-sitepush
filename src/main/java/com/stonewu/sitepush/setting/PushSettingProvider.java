package com.stonewu.sitepush.setting;

import reactor.netty.transport.ProxyProvider;

/**
 * @author Erzbir
 * @Date 2023/10/17
 */
public interface PushSettingProvider {
    String getConfigMapName();

    String getGroup();

    Boolean isEnable();

    /**
     * @return 是否启用标签验证
     */
    Boolean isTagVerificationEnable();

    /**
     * @return 设置的字节码
     */
    Class<?> getSettingClass();

    /**
     * @return 站点验证码
     */
    String getSiteVerification();

    /**
     * @return 访问口令
     */
    String getAccess();

    /**
     * @return html tag 验证的 meta 标签
     */
    String getSiteVerificationMeta();

    Boolean isUseProxy();

    ProxyProvider.Proxy getProxyType();

    String getProxyAddress();

    Integer getProxyPort();

    Boolean proxyAuthEnable();

    String getProxyUsername();

    String getProxyPassword();
}
