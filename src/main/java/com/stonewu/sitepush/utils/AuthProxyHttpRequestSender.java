package com.stonewu.sitepush.utils;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.Getter;
import lombok.Setter;
import reactor.netty.http.client.HttpClient;

/**
 * @author Erzbir
 * @Date 2023/10/23
 */
@Getter
@Setter
public class AuthProxyHttpRequestSender extends AbstractHttpRequestSender
    implements HttpRequestSender {
    private final ProxyHttpRequestSender proxyHttpRequestSender;
    private String username;
    private String password;

    public AuthProxyHttpRequestSender(String username, String password, Proxy proxy) {
        proxyHttpRequestSender = new ProxyHttpRequestSender(proxy);
        this.username = username;
        this.password = password;
    }


    @Override
    public boolean isProxy() {
        return true;
    }

    @Override
    protected HttpClient.RequestSender getRequestSender(HttpMethod httpMethod,
        HttpHeaders httpHeaders) {
        final String data = username.concat(":").concat(password);
        String token =
            "Basic " + Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
        httpHeaders.add(HttpHeaderNames.PROXY_AUTHORIZATION, token);
        return proxyHttpRequestSender.getRequestSender(httpMethod, httpHeaders);
    }
}
