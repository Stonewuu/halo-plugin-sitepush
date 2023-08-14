package com.stonewu.sitepush;

import com.stonewu.sitepush.service.PushService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SitePushEndpoint {

    private PushService pushService;


}
