package com.stonewu.sitepush.job;

import com.stonewu.sitepush.scheme.PushLog;
import com.stonewu.sitepush.scheme.PushUnique;
import com.stonewu.sitepush.setting.BasePushSetting;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.extension.ListOptions;
import run.halo.app.extension.index.query.QueryFactory;
import run.halo.app.extension.router.selector.FieldSelector;
import run.halo.app.plugin.SettingFetcher;

@Component
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class CleanOldDataJob {
    private SettingFetcher settingFetcher;

    private ExtensionClient client;

    @Scheduled(cron = "0 0 0 1/1 * ?")
    public void clean() {
        BasePushSetting basePushSetting =
            settingFetcher.fetch(BasePushSetting.GROUP, BasePushSetting.class)
                .orElseGet(BasePushSetting::new);
        Integer cleanOldDataDays = basePushSetting.getCleanOldLogDataDays();
        Integer cleanOldUniqueDataDays = basePushSetting.getCleanOldUniqueDataDays();
        if (cleanOldDataDays != null && cleanOldDataDays > 0) {
            long oldTime = Instant.now().getEpochSecond() - cleanOldDataDays * 24 * 60 * 60;
            Instant instant = Instant.ofEpochSecond(oldTime);
            ListOptions listOptions = getListOptions(instant);
            List<PushLog> pushLogs = client.listAll(PushLog.class, listOptions, null);
            pushLogs.forEach(log -> {
                client.delete(log);
            });
            log.info("已清理 {} 之前的 {} 条log数据", instant, pushLogs.size());
        }
        if (cleanOldUniqueDataDays != null && cleanOldUniqueDataDays > 0) {
            long oldTime = Instant.now().getEpochSecond() - cleanOldUniqueDataDays * 24 * 60 * 60;
            Instant instant = Instant.ofEpochSecond(oldTime);
            ListOptions listOptions = getListOptions(instant);
            List<PushUnique> pushUniques = client.listAll(PushUnique.class, listOptions, null);
            pushUniques.forEach(unique -> {
                client.delete(unique);
            });
            log.info("已清理 {} 之前的 {} 条key数据", instant, pushUniques.size());
        }
    }

    private static ListOptions getListOptions(Instant instant) {
        FieldSelector fieldSelector = FieldSelector.of(
            QueryFactory.lessThan("metadata.creationTimestamp", String.valueOf(instant)));
        ListOptions listOptions = new ListOptions();
        listOptions.setFieldSelector(fieldSelector);
        return listOptions;
    }
}
