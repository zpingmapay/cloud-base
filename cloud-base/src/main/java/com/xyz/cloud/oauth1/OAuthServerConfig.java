package com.xyz.cloud.oauth1;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties("cloud.server")
public class OAuthServerConfig {
    private Map<String, OAuthConfig> oauth;

    public String findConsumerSecretByKey(String consumerKey) {
        return oauth.values().stream().filter(x -> consumerKey.equals(x.key)).map(OAuthConfig::getSecret).findAny().orElse(null);
    }

    public String findBaseUrlByKey(String consumerKey) {
        return oauth.values().stream().filter(x -> consumerKey.equals(x.key)).map(OAuthConfig::getBaseUrl).findAny().orElse(null);
    }

    @Data
    public static class OAuthConfig {
        private String appId;
        private String baseUrl;
        private String key;
        private String secret;
    }

}
