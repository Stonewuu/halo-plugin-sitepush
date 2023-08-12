package run.halo.sitepush.strategy;

import java.util.HashMap;
import java.util.Map;

public interface PushStrategy {
    String getPushType();

    /**
     *
     * @param siteUrl 站点地址，不含/，例：https://www.stonewu.com
     * @param key 用于缓存已经推送的key
     * @param pageLink 页面绝对访问路径，以/开头，例：/post/new-page
     * @return
     */
    boolean push(String siteUrl, String key,String pageLink);

}