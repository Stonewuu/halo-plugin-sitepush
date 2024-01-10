package com.stonewu.sitepush.service;

import com.stonewu.sitepush.strategy.PushStrategy;

public interface PushService {
    boolean pushUseAllStrategy(String siteUrl, String slugKey, String... permalinks);

    boolean push(String siteUrl, PushStrategy pushStrategy, String slug, String... permalinks);
}
