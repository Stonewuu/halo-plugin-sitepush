package com.stonewu.sitepush.utils;

import java.net.SocketAddress;
import reactor.netty.transport.ProxyProvider;

/**
 * @author Erzbir
 * @Date 2023/10/29
 */

public record Proxy(ProxyProvider.Proxy type, SocketAddress address) {

}
