package com.stonewu.sitepush.setting;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BaiduPushSetting {

    public static final String CONFIG_MAP_NAME = "plugin-sitepush-config";

    public static final String GROUP = "baidu";

    private String siteVerification = "";

    private Boolean baiduEnable = Boolean.FALSE;

    private String token = "";

}
