package com.xyz.oauth1;

import com.xyz.utils.JsonConfig;

import java.util.Comparator;
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
        return keys.stream().filter(x -> consumerKey.equals(x.getKey())).map(OAuthKey::getSecret).findAny().orElse(null);
    }

    @Override
    public OAuthKey findByUrl(String url) {
        return keys.stream().filter(x -> url.startsWith(x.getUrl())).max(Comparator.comparingInt(x -> x.getUrl().length())).orElse(null);
    }

    @Override
    public OAuthKey findByName(String name) {
        return keys.stream().filter(x -> name.equals(x.getName())).findAny().orElse(null);
    }
}
