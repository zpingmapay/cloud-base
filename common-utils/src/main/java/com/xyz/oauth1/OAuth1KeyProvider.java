package com.xyz.oauth1;

public interface OAuth1KeyProvider {
    String findConsumerSecretByKey(String consumerKey);

    OAuthKey findByServiceName(String serviceName);

    OAuthKey findByAppId(String appId);
}
