package com.stonewu.sitepush.utils;


import io.netty.channel.ChannelOption;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;
import reactor.netty.ByteBufFlux;
import reactor.netty.http.HttpProtocol;
import reactor.netty.http.client.HttpClient;

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
        httpClient = HttpClient.create().keepAlive(false)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeOut);
        httpClient.responseTimeout(Duration.ofMillis(timeOut));
    }

    protected AbstractHttpRequestSender() {
        this(5000);
    }

    @Override
    public Mono<HttpResponse> request(String requestUrl, HttpMethod httpMethod,
        HttpHeaders httpHeaders, String body) {
        httpClient = httpClient.protocol(HttpProtocol.HTTP11);
        return getRequestSender(httpMethod, httpHeaders)
            .uri(requestUrl)
            .send(ByteBufFlux.fromString(Mono.just(body)))
            .responseSingle((response, byteBufMono) -> byteBufMono.map(
                byteBuf -> new HttpResponse(response.status().code(), byteBuf)))
            .doOnError(throwable -> new HttpResponse(500, throwable.getMessage()));
    }

    protected abstract HttpClient.RequestSender getRequestSender(HttpMethod httpMethod,
        HttpHeaders httpHeaders);
}
