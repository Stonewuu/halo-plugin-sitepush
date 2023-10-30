package com.stonewu.sitepush.utils;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import java.net.InetSocketAddress;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

/**
 * @author Erzbir
 * @Date 2023/10/23
 */
@Getter
@Setter
@Slf4j
public class ProxyHttpRequestSender extends AbstractHttpRequestSender implements HttpRequestSender {
    private Proxy proxy;

    public ProxyHttpRequestSender(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public HttpClient.RequestSender getRequestSender(HttpMethod httpMethod,
        HttpHeaders httpHeaders) {
        return httpClient
            .headers(builder -> builder.add(httpHeaders))
            .proxy(proxyOptions -> proxyOptions.type(proxy.type())
                .address((InetSocketAddress) proxy.address()))
            .request(httpMethod);
    }

    @Override
    public boolean isProxy() {
        return true;
    }
}
