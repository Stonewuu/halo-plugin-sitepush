package run.halo.sitepush;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import run.halo.app.core.extension.content.Category;
import run.halo.app.core.extension.content.Post;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.MetadataUtil;
import run.halo.app.extension.controller.Controller;
import run.halo.app.extension.controller.ControllerBuilder;
import run.halo.app.extension.controller.Reconciler;

/**
 * Reconciler for {@link Category}.
 *
 * @author guqing
 * @since 2.0.0
 */
@Component
@AllArgsConstructor
@Slf4j
public class PostPushReconciler implements Reconciler<Reconciler.Request> {


    private final ExtensionClient client;


    @Override
    public Result reconcile(Request request) {
        return client.fetch(Post.class, request.name())
            .map(post -> {
                if(post.isPublished()){
                    String slug = post.getSpec().getSlug();
                    String permalink = post.getStatus().getPermalink();
                    boolean totalPush = PushStrategy.isTotalPush(slug, permalink);
                    if(totalPush){
                        return Result.doNotRetry();
                    }
                    return new Result(true, Duration.ofMinutes(1));
                }
                return Result.doNotRetry();
            })
            .orElseGet(() -> new Result(false, null));

    }

    @Override
    public Controller setupWith(ControllerBuilder builder) {
        return builder
            .extension(new Post())
            .build();
    }


}
