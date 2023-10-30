package com.stonewu.sitepush.utils;


import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
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
public class DefaultHttpRequestSender extends AbstractHttpRequestSender
    implements HttpRequestSender {

    @Override
    public boolean isProxy() {
        return false;
    }

    @Override
    public HttpClient.RequestSender getRequestSender(HttpMethod httpMethod,
        HttpHeaders httpHeaders) {
        return httpClient
            .headers(builder -> builder.add(httpHeaders))
            .request(httpMethod);
    }
}
