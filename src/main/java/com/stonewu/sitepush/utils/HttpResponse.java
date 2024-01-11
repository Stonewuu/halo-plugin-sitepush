package com.stonewu.sitepush.utils;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

/**
 * @author Erzbir
 * @Date 2023/10/30
 */
public class HttpResponse {
    private final int code;
    private final String body;

    public HttpResponse(int code, ByteBuf byteBuf) {
        this(code, byteBuf.toString(StandardCharsets.UTF_8));
    }

    public HttpResponse(int code, String body) {
        this.code = code;
        this.body = body;
    }

    public int code() {
        return code;
    }

    public String body() {
        return body;
    }
}
