package com.stonewu.sitepush.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

/**
 * @author Erzbir
 * @Date 2023/10/23
 */
public interface HttpRequestSender {
    HttpResponse request(HttpRequest request);

    boolean isProxy();
}
