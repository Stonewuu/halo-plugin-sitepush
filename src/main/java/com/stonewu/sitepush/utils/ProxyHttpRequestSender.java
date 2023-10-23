package com.stonewu.sitepush.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import java.net.Proxy;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Erzbir
 * @Date 2023/10/23
 */
@Getter
@Setter
public class ProxyHttpRequestSender implements HttpRequestSender {
    private Proxy proxy;
    private DefaultHttpRequestSender defaultHttpRequestSender;

    public ProxyHttpRequestSender(Proxy proxy) {
        this.proxy = proxy;
        defaultHttpRequestSender = new DefaultHttpRequestSender();
    }

    @Override
    public HttpResponse request(HttpRequest request) {
        return defaultHttpRequestSender.request(request.setProxy(proxy));
    }

    @Override
    public boolean isProxy() {
        return true;
    }
}
