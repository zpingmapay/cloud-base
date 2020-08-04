package com.xyz.oauth1;

public interface OAuth1KeyProvider {
    String HEADER_AUTH_TOKEN = "Authorization";

    String findConsumerSecretByKey(String consumerKey);

    OAuthKey findByHost(String host);
}
