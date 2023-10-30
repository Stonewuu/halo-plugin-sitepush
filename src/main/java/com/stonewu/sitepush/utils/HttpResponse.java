package com.stonewu.sitepush.utils;

import reactor.core.publisher.Mono;

/**
 * @author Erzbir
 * @Date 2023/10/30
 */
public record HttpResponse(int code, Mono<String> body) {
}
