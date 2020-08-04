package com.xyz.cloud.oauth1;

import com.xyz.oauth1.JsonConfigKeyProvider;
import com.xyz.oauth1.OAuth1KeyProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OAuth1Configuration {
    @Bean
    @ConditionalOnMissingBean(OAuth1KeyProvider.class)
    public OAuth1KeyProvider oAuth1KeyProvider(@Value("${cloud.oauth1.config-folder: /etc/xyz/config/}") String configFolder,
                                               @Value("${cloud.oauth1.config-name: oauth1-keys.json}") String configName) {
        return new JsonConfigKeyProvider(configFolder, configName);
    }

    @Bean
    public OAuth1Validator oAuth1Validator(OAuth1KeyProvider keyProvider) {
        return new OAuth1Validator(keyProvider);
    }

    @Bean
    public OAuth1Aspect oAuth1Aspect(OAuth1Validator oAuth1Validator) {
        return new OAuth1Aspect(oAuth1Validator);
    }
}
