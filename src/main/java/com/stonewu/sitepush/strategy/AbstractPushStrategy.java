package com.stonewu.sitepush.strategy;

import com.stonewu.sitepush.scheme.PushLog;
import com.stonewu.sitepush.setting.PushSettingProvider;
import com.stonewu.sitepush.utils.AuthProxyHttpRequestSender;
import com.stonewu.sitepush.utils.DefaultHttpRequestSender;
import com.stonewu.sitepush.utils.HttpRequestSender;
import com.stonewu.sitepush.utils.HttpResponse;
import com.stonewu.sitepush.utils.Proxy;
import com.stonewu.sitepush.utils.ProxyHttpRequestSender;
import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import run.halo.app.extension.Metadata;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.plugin.SettingFetcher;

/**
 * @author Erzbir
 * @Date 2023/10/23
 */
@Slf4j
public abstract class AbstractPushStrategy implements PushStrategy {
    protected final SettingFetcher settingFetcher;
    protected final ReactiveExtensionClient client;

    protected HttpRequestSender httpRequestSender;

    public AbstractPushStrategy(SettingFetcher settingFetcher, ReactiveExtensionClient client) {
        this.settingFetcher = settingFetcher;
        this.client = client;
    }

    @Override
    public int push(String siteUrl, String key, String... pageLinks) {
        PushSettingProvider settingProvider = getSettingProvider();
        updateHttpRequestSender(settingProvider);
        String token = settingProvider.getAccess();
        if (settingProvider.isEnable() && StringUtils.hasText(token)) {
            HttpResponse response;
            try {
                response = request(settingProvider, siteUrl, pageLinks).block();
                if (response == null) {
                    throw new Exception("response is null");
                }
            } catch (Exception e) {
                log.warn("Push exception: {} : {}", getPushType(), e.getMessage());
                recordPushLogs(0, e.getMessage(), siteUrl, pageLinks);
                return 0;
            }
            String body = response.body();
            log.info("Pushing to {} Result: {}", getPushType(), body);
            log.info("code: {}", response.code());
            boolean status = response.code() == 200;
            int result = status ? 1 : 0;
            recordPushLogs(result, body, siteUrl, pageLinks);
            return result;
        }
        return -1;

    }

    private void recordPushLogs(int status, String remark, String siteUrl, String... pageLinks) {
        for (String pageLink : pageLinks) {
            PushLog pushLog = new PushLog(Instant.now().getEpochSecond(), siteUrl + pageLink,
                getPushType(), status, remark);
            Metadata metadata = new Metadata();
            metadata.setName(UUID.randomUUID().toString());
            pushLog.setMetadata(metadata);
            client.create(pushLog).subscribe();
        }
    }

    /**
     * 通过设置更新请求策略
     */
    protected void updateHttpRequestSender(PushSettingProvider settingProvider) {
        if (settingProvider.isUseProxy()) {
            Proxy proxy = new Proxy(settingProvider.getProxyType(), new InetSocketAddress(
                settingProvider.getProxyAddress(), settingProvider.getProxyPort()));
            if (settingProvider.proxyAuthEnable()) {
                httpRequestSender =
                    new AuthProxyHttpRequestSender(settingProvider.getProxyUsername(),
                        settingProvider.getProxyPassword(), proxy);
            } else {
                httpRequestSender = new ProxyHttpRequestSender(proxy);
            }
        } else {
            httpRequestSender = new DefaultHttpRequestSender();
        }
    }

    protected abstract PushSettingProvider getSettingProvider();

    protected abstract Mono<HttpResponse> request(PushSettingProvider settingProvider,
        String siteUrl, String... pageLinks) throws Exception;
}
