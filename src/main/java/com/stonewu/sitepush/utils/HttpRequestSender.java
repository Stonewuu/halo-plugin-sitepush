package com.stonewu.sitepush.utils;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import reactor.core.publisher.Mono;

/**
 * @author Erzbir
 * @Date 2023/10/23
 */
public interface HttpRequestSender {
    Mono<HttpResponse> request(String requestUrl, HttpMethod httpMethod, HttpHeaders httpHeaders,
        String body);

    boolean isProxy();
}
