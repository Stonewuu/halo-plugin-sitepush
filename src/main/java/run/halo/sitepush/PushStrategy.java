package run.halo.sitepush;

import java.util.HashMap;
import java.util.Map;

public interface PushStrategy {
    String getPushType();

    boolean push(String pageName);

    public static boolean isTotalPush(String slug, String permalink) {
        boolean totalPush = true;
        for (Map.Entry<String, PushStrategy> entry : GlobalConfig.pushStrategyMap.entrySet()) {
            String key = entry.getKey();
            PushStrategy strategy = entry.getValue();
            Map<String, Boolean> cacheMap =
                GlobalConfig.pushCache.getOrDefault(strategy.getPushType(),
                    new HashMap<>());
            if (cacheMap.get(slug) != null) {
                boolean pushed = strategy.push(permalink);
                if (!pushed) {
                    totalPush = pushed;
                }

            }
        }
        return totalPush;
    }

}
