package run.halo.sitepush.strategy;

import java.util.HashMap;
import java.util.Map;

public interface PushStrategy {
    String getPushType();

    boolean push(String key,String pageName);

}
