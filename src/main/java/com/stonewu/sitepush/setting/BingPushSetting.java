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
}
