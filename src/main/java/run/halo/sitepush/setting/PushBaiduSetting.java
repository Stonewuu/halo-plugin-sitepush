package run.halo.sitepush.setting;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PushBaiduSetting {

    public static final String CONFIG_MAP_NAME = "plugin-sitepush-config";

    public static final String GROUP = "baidu";

    private Boolean baiduEnable = Boolean.FALSE;

    private String token = "";

}
