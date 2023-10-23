package com.stonewu.sitepush.setting;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BingPushSetting {

    public static final String CONFIG_MAP_NAME = "plugin-sitepush-config";

    public static final String GROUP = "bing";

    private Boolean bingEnable = Boolean.FALSE;

    private String apikey = "";

    private String bingSiteVerification = "";

    private Boolean bingEnableTagVerification = Boolean.TRUE;

    private Boolean bingProxyEnable = Boolean.FALSE;

    private String bingProxyType = "HTTP";

    private String bingProxyAddress = "";

    private Integer bingProxyPort = 0;

    private Boolean bingProxyAuthEnable = Boolean.FALSE;

    private String bingProxyUsername = "";

    private String bingProxyPassword = "";
}
