package com.xyz.cloud.oauth1.provider;

import com.xyz.utils.JsonConfig;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonConfigKeyProvider implements OAuth1KeyProvider {
    private final String configFolder;
    private final String configName;
    private final Map<String, String> keys;

    public JsonConfigKeyProvider(String configFolder, String configName) {
        this.configFolder = configFolder;
        this.configName = configName;
        this.keys = loadKeys();
    }

    private Map<String, String> loadKeys() {
        List<OAuthKey> oAuthKeys = JsonConfig.config2List(this.configFolder, this.configName, OAuthKey.class);
        return oAuthKeys.stream().collect(Collectors.toMap(OAuthKey::getKey, OAuthKey::getSecret));
    }

    @Override
    public String findConsumerSecretByKey(String consumerKey) {
        return keys.get(consumerKey);
    }

    @Data
    public static class OAuthKey {
        private String key;
        private String secret;
        private String desc;
    }
}
