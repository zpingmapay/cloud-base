package com.xyz.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;
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
        @NotBlank(message = "cloud.client.oauth.*.url is not allow empty")
        private String url;
        @NotBlank(message = "cloud.client.oauth.*.key is not allow empty")
        private String key;
        @NotBlank(message = "cloud.client.oauth.*.secret is not allow empty")
        private String secret;
    }

    @Data
    public static class SignerConfig {
        @NotBlank(message = "cloud.client.signer.*.url is not allow empty")
        private String url;
        @NotBlank(message = "cloud.client.signer.*.appId is not allow empty")
        private String appId;
        @NotBlank(message = "cloud.client.signer.*.appKey is not allow empty")
        private String appKey;
    }

}
