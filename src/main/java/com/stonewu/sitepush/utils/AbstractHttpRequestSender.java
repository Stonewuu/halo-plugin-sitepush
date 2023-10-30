package com.stonewu.sitepush.utils;


import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * @author Erzbir
 * @Date 2023/10/29
 */
@Getter
@Setter
public abstract class AbstractHttpRequestSender implements HttpRequestSender {
    protected int timeOut;
    protected HttpClient httpClient;

    protected AbstractHttpRequestSender(int timeOut) {
        this.timeOut = timeOut;
        httpClient = HttpClient.create();
        httpClient.responseTimeout(Duration.ofMillis(timeOut));
    }

    protected AbstractHttpRequestSender() {
        this(5000);
    }

    @Override
    public Mono<HttpResponse> request(String requestUrl, HttpMethod httpMethod,
        HttpHeaders httpHeaders, String body) {
        httpClient = httpClient.protocol(HttpProtocol.HTTP11).baseUrl(requestUrl);
        return getRequestSender(httpMethod, httpHeaders)
            .send(ByteBufFlux.fromString(Mono.just(body)))
            .responseSingle((response, byteBufMono) -> Mono.just(
                new HttpResponse(response.status().code(), byteBufMono.asString())));
    }

    protected abstract HttpClient.RequestSender getRequestSender(HttpMethod httpMethod,
        HttpHeaders httpHeaders);
}
