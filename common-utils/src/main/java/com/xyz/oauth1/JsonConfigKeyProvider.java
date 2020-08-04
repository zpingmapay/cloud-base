package com.xyz.oauth1;

import com.xyz.utils.JsonConfig;

import java.util.List;

public class JsonConfigKeyProvider implements OAuth1KeyProvider {
    private final String configFolder;
    private final String configName;
    private final List<OAuthKey> keys;

    public JsonConfigKeyProvider(String configFolder, String configName) {
        this.configFolder = configFolder;
        this.configName = configName;
        this.keys = loadKeys();
    }

    private List<OAuthKey> loadKeys() {
        return JsonConfig.config2List(this.configFolder, this.configName, OAuthKey.class);
    }

    @Override
    public String findConsumerSecretByKey(String consumerKey) {
        return keys.stream().filter(x -> consumerKey.equals(x.getKey())).map(x -> x.getSecret()).findAny().orElse(null);
    }

    @Override
    public OAuthKey findByHost(String host) {
        return keys.stream().filter(x -> host.equals(x.getHost())).findAny().orElse(null);
    }
}
