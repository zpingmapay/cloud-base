package com.xyz.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Comparator;
import java.util.Map;

@Data
@ConfigurationProperties("cloud.client")
public class ClientCredentialConfig {
    private Map<String, OAuthConfig> oauth;
    private Map<String, SignerConfig> signer;

    public OAuthConfig findOAuthConfigByUrl(String url) {
        return oauth.values().stream().filter(x -> url.startsWith(x.url)).max(Comparator.comparingInt(x -> x.url.length())).orElse(null);
    }

    public SignerConfig findSignerConfigByUrl(String url) {
        return signer.values().stream().filter(x -> url.startsWith(x.url)).max(Comparator.comparingInt(x -> x.url.length())).orElse(null);
    }

    @Data
    public static class OAuthConfig {
        private String url;
        private String key;
        private String secret;
    }

    @Data
    public static class SignerConfig {
        private String url;
        private String appId;
        private String appKey;
    }

}
