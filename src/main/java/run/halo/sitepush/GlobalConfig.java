package run.halo.sitepush;

import java.util.HashMap;
import java.util.Map;

public class GlobalConfig {

    public static Map<String, PushStrategy> pushStrategyMap = new HashMap<>();

    public static Map<String, Map<String, Boolean>> pushCache = new HashMap<>();

}
