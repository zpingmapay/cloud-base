package com.xyz.cloud.oauth1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth1Configuration {

    @Bean
    public OAuth1Validator oAuth1Validator(OAuthServerConfig config) {
        return new OAuth1Validator(config);
    }

    @Bean
    public OAuth1Aspect oAuth1Aspect(OAuth1Validator oAuth1Validator) {
        return new OAuth1Aspect(oAuth1Validator);
    }
}
