package com.xyz.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Comparator;
import java.util.Map;

@Data
@ConfigurationProperties("cloud.client")
public class OAuthClientConfig {
    private Map<String, OAuthConfig> oauth;

    @Data
    public static class OAuthConfig {
        private String url;
        private String key;
        private String secret;
    }

    public OAuthConfig findByUrl(String url) {
        return oauth.values().stream().filter(x -> url.startsWith(x.url)).max(Comparator.comparingInt(x -> x.url.length())).orElse(null);
    }
}
