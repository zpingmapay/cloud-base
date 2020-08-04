package com.xyz.cloud.oauth1.provider;

import lombok.Data;

public interface OAuth1KeyProvider {
    String findConsumerSecretByKey(String consumerKey);

    @Data
    class OAuthKey {
        private String appId;
        private String serviceName;
        private String key;
        private String secret;
        private String desc;
    }
}
