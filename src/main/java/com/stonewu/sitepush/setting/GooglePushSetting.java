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

    private String private_key_json = "";

    private String googleSiteVerification = "";

    private Boolean isDomainVerification = Boolean.FALSE;
}
