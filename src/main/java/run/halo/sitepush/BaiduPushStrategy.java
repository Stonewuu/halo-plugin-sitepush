package run.halo.sitepush;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.springframework.stereotype.Component;
import run.halo.app.extension.controller.Reconciler;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Component
public class BaiduPushStrategy implements PushStrategy {
    {
        GlobalConfig.pushStrategyMap.put("baidu", this);
    }

    @Override
    public String getPushType() {
        return "baidu";
    }

    @Override
    public boolean push(String pageLink) {

        Map<String, Boolean> baidu =
            GlobalConfig.pushCache.getOrDefault(this.getPushType(), new HashMap<>());
        // TODO 判断是否开启推送

        // TODO 增加推送配置
        String baiduPushUrl =
            String.format("http://data.zz.baidu.com/urls?site=%s&token=%s",
                "website" + pageLink, "key");
        HttpResponse execute = HttpRequest.get(baiduPushUrl).execute();
        if (execute.isOk()) {
            baidu.put(this.getPushType() + ":" + pageLink, true);
            return true;
        } else {
            return false;
        }
    }
}
