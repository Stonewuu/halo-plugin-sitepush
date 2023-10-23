package com.stonewu.sitepush.setting;

import lombok.Data;
import lombok.ToString;

/**
 * @author Erzbir
 * @Date 2023/10/12
 */
@Data
@ToString
public class GooglePushSetting {
    public static final String CONFIG_MAP_NAME = "plugin-sitepush-config";

    public static final String GROUP = "google";

    private Boolean googleEnable = Boolean.FALSE;

    private String credentialsJson = "";

    private String googleSiteVerification = "";

    private Boolean googleEnableTagVerification = Boolean.TRUE;

    private Boolean googleProxyEnable = Boolean.FALSE;

    private String googleProxyType = "HTTP";

    private String googleProxyAddress = "";

    private Integer googleProxyPort = 0;

    private Boolean googleProxyAuthEnable = Boolean.FALSE;

    private String googleProxyUsername = "";

    private String googleProxyPassword = "";
}
