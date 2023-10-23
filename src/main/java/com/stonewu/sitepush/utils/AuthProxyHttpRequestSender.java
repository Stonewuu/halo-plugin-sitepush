package com.stonewu.sitepush.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.Getter;
import lombok.Setter;

import java.net.Proxy;

/**
 * @author Erzbir
 * @Date 2023/10/23
 */
@Getter
@Setter
public class AuthProxyHttpRequestSender implements HttpRequestSender {
    private String username;
    private String password;
    private final ProxyHttpRequestSender proxyHttpRequestSender;

    public AuthProxyHttpRequestSender(String username, String password, Proxy proxy) {
        proxyHttpRequestSender = new ProxyHttpRequestSender(proxy);
        this.username = username;
        this.password = password;
    }


    @Override
    public HttpResponse request(HttpRequest request) {
        request = request.basicProxyAuth(username, password);
        return proxyHttpRequestSender.request(request);
    }

    @Override
    public boolean isProxy() {
        return true;
    }
}
