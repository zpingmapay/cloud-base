package com.xyz.cloud.oauth1.provider;

public interface OAuth1KeyProvider {
    String findConsumerSecretByKey(String consumerKey);
}
