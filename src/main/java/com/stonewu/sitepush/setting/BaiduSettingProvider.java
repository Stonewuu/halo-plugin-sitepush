package com.stonewu.sitepush.setting;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Erzbir
 * @Date 2023/10/17
 */
@AllArgsConstructor
public class BaiduSettingProvider implements PushSettingProvider {
    private BaiduPushSetting baiduPushSetting;

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
        return baiduPushSetting.getBaiduEnable();
    }

    @Override
    public Boolean isTagVerificationEnable() {
        return baiduPushSetting.getBaiduEnableTagVerification();
    }

    @Override
    public Class<?> getSettingClass() {
        return baiduPushSetting.getClass();
    }

    @Override
    public String getSiteVerification() {
        return baiduPushSetting.getSiteVerification();
    }

    @Override
    public String getAccess() {
        return baiduPushSetting.getToken();
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
}
