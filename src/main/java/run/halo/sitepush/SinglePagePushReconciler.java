package run.halo.sitepush;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import run.halo.app.core.extension.content.SinglePage;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.ExtensionOperator;
import run.halo.app.extension.controller.Controller;
import run.halo.app.extension.controller.ControllerBuilder;
import run.halo.app.extension.controller.Reconciler;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link SinglePagePushReconciler} for route request to specific template <code>page.html</code>.
 *
 * @author guqing
 * @since 2.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SinglePagePushReconciler
    implements Reconciler<Reconciler.Request> {
    private Map<NameSlugPair, HandlerFunction<ServerResponse>> quickRouteMap =
        new ConcurrentHashMap<>();

    private final ExtensionClient client;


    @Override
    public Result reconcile(Request request) {
        client.fetch(SinglePage.class, request.name()).map(
            page -> {
                if(page.isPublished()){
                    String slug = page.getSpec().getSlug();
                    String permalink = page.getStatus().getPermalink();
                    boolean totalPush = PushStrategy.isTotalPush(slug, permalink);
                    if(totalPush){
                        return Result.doNotRetry();
                    }
                    return new Result(true, Duration.ofMinutes(1));
                }
                return Result.doNotRetry();
            });
        return new Result(false, null);
    }

    @Override
    public Controller setupWith(ControllerBuilder builder) {
        return builder
            .extension(new SinglePage())
            .build();
    }

    record NameSlugPair(String name, String slug) {
        public static NameSlugPair from(SinglePage page) {
            return new NameSlugPair(page.getMetadata().getName(), page.getSpec().getSlug());
        }
    }

    String singlePageRoute(String slug) {
        return StringUtils.prependIfMissing(slug, "/");
    }
}
