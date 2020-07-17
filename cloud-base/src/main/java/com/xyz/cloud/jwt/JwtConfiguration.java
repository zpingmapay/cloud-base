package com.xyz.cloud.jwt;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfiguration {
    @Bean
    public JwtConfig jwtConfig(@Value("${cloud.jwt.app-id:0}") String appId,
                               @Value("${cloud.jwt.secret:20200808qa1zwsxedc3rfv4tgbyhnujm5ikolp9ttt$QA0ZWSXED2CRFV8TGB2YHNU7JM3IKO8LPa12*@(cloud.jwt)}") String secret,
                               @Value("${cloud.jwt.ttl-in-hours:720}") int ttlInHours,
                               @Value("${cloud.jwt.multi-login-check:false}") boolean multiLoginCheck) {
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setAppId(appId);
        jwtConfig.setSecretSeed(secret);
        jwtConfig.setTtlInHours(ttlInHours);
        jwtConfig.setMultiLoginCheck(multiLoginCheck);
        return jwtConfig;
    }

    @Bean("JwtTokenProviderWithoutCache")
    @ConditionalOnMissingBean(JwtTokenProvider.class)
    public JwtTokenProvider tokenProviderWithoutCache(JwtConfig jwtConfig) {
        return new JwtTokenProvider(jwtConfig);
    }

    @Bean("JwtTokenProviderWithCache")
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnMissingBean(JwtTokenProvider.class)
    public JwtTokenProvider tokenProviderWithCache(JwtConfig jwtConfig, RedissonClient redissonClient) {
        return new JwtTokenProvider(jwtConfig, redissonClient);
    }

    @Bean
    public JwtAspect jwtAspect(JwtTokenProvider jwtTokenProvider) {
        return new JwtAspect(jwtTokenProvider);
    }
}
