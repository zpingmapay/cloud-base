package com.xyz.cloud.jwt;

import com.xyz.cache.CacheManager;
import com.xyz.cache.ICache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.xyz.cloud.jwt.JwtTokenProvider.CACHE_NAMESPACE;

@Configuration
public class JwtConfiguration {
    @Bean
    public JwtConfig jwtConfig(@Value("${cloud.jwt.app-id:0}") String appId,
                               @Value("${cloud.jwt.secret:20200808qa1zwsxedc3rfv4tgbyhnujm5ikolp9ttt$QA0ZWSXED2CRFV8TGB2YHNU7JM3IKO8LPa12*@(cloud.jwt)}") String secret,
                               @Value("${cloud.jwt.ttl-in-hours:720}") int ttlInHours,
                               @Value("${cloud.jwt.multi-login-check:true}") boolean multiLoginCheck) {
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setAppId(appId);
        jwtConfig.setSecretSeed(secret);
        jwtConfig.setTtlInHours(ttlInHours);
        jwtConfig.setMultiLoginCheck(multiLoginCheck);
        return jwtConfig;
    }

    @Bean("JwtTokenProviderWithLocalCache")
    @ConditionalOnMissingBean(RedissonClient.class)
    public JwtTokenProvider tokenProviderWithLocalCache(JwtConfig jwtConfig) {
        String namespace = CACHE_NAMESPACE.concat(jwtConfig.getAppId());
        ICache<String, String> cache = CacheManager.getLocalCache(namespace);
        return new JwtTokenProvider(jwtConfig, cache);
    }

    @Bean("JwtTokenProviderWithRedisCache")
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnMissingBean(JwtTokenProvider.class)
    public JwtTokenProvider tokenProviderWithRedisCache(JwtConfig jwtConfig, RedissonClient redissonClient) {
        String namespace = CACHE_NAMESPACE.concat(jwtConfig.getAppId());
        ICache<String, String> cache = CacheManager.getRedisCache(namespace, redissonClient);
        return new JwtTokenProvider(jwtConfig, cache);
    }

    @Bean
    public JwtAspect jwtAspect(JwtTokenProvider jwtTokenProvider, ApplicationContext ctx) {
        return new JwtAspect(jwtTokenProvider, ctx);
    }
}
