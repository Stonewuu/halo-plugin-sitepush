package com.stonewu.sitepush.setting;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BasePushSetting {
    public static final String CONFIG_MAP_NAME = "plugin-sitepush-config";

    public static final String GROUP = "basic";

    private Boolean enable = Boolean.FALSE;

    private String siteUrl = "";

    private Integer retryInterval = 360;

    private Integer retryCount = 3;

    private Integer cleanOldLogDataDays = 0;

    private Integer cleanOldUniqueDataDays = 0;
}
