package com.stonewu.sitepush.strategy;

public interface PushStrategy {
    String getPushType();

    /**
     * @param siteUrl 站点地址，不含/，例：https://www.stonewu.com
     * @param key 用于缓存已经推送的key
     * @param pageLinks 页面绝对访问路径，以/开头，例：/post/new-page
     * @return 返回 -1 为没有进行推送, 1 为推送成功, 0 为推送失败
     */
    int push(String siteUrl, String key, String... pageLinks);

}
