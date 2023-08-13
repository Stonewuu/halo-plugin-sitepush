package run.halo.sitepush;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import run.halo.sitepush.service.PushService;

@Component
@AllArgsConstructor
public class SitePushEndpoint {

    private PushService pushService;


}
