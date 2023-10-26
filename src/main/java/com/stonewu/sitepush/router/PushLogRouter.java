package com.stonewu.sitepush.router;

import com.stonewu.sitepush.scheme.PushLog;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import run.halo.app.extension.ReactiveExtensionClient;
import run.halo.app.plugin.ApiVersion;

/**
 * @author Erzbir
 * @Date 2023/10/26
 */
@ApiVersion("v1alpha1")
@RestController
@RequestMapping("/pushLogs")
@AllArgsConstructor
public class PushLogRouter {
    private ReactiveExtensionClient client;

    @DeleteMapping("/clear")
    public Mono<Void> clearLogs() {
        return client.list(PushLog.class, null, null).flatMap(pushLog -> client.delete(pushLog))
            .then();
    }
}
