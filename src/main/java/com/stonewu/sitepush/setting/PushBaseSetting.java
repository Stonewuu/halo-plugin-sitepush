package com.stonewu.sitepush.setting;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PushBaseSetting {
    public static final String CONFIG_MAP_NAME = "plugin-sitepush-config";
    public static final String GROUP = "basic";

    private Boolean enable = Boolean.FALSE;

    private String siteUrl = "";

}
