package run.halo.sitepush;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import run.halo.app.extension.ConfigMap;
import run.halo.app.extension.ExtensionClient;
import run.halo.app.infra.utils.JsonUtils;

import java.util.Map;
import java.util.Optional;

@Component
@AllArgsConstructor
public class DefaultSettingFetcher {

    private final ExtensionClient extensionClient;

    public <T> Optional<T> fetch(String configMapName, String group, Class<T> clazz) {
        return getValuesInternal(configMapName)
                .filter(map -> map.containsKey(group))
                .map(map -> map.get(group))
                .map(stringValue -> JsonUtils.jsonToObject(stringValue, clazz));
    }


    private Optional<Map<String, String>> getValuesInternal(String configMapName) {
        return getConfigMap(configMapName)
                .filter(configMap -> configMap.getData() != null)
                .map(ConfigMap::getData);
    }

    private Optional<ConfigMap> getConfigMap(String configMapName) {
        return extensionClient.fetch(ConfigMap.class, configMapName);
    }
}
