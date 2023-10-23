package com.stonewu.sitepush.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Erzbir
 * @Date 2023/10/23
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DefaultHttpRequestSender implements HttpRequestSender {
    private int timeOut = 5000;

    @Override
    public HttpResponse request(HttpRequest request) {
        return request.timeout(timeOut).setConnectionTimeout(timeOut).setReadTimeout(timeOut)
            .execute();
    }

    @Override
    public boolean isProxy() {
        return false;
    }
}
