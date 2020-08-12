package com.xyz.cloud.sample;

import com.xyz.cloud.retry.repository.EventRepositoryFactory;
import com.xyz.cloud.retry.deadevent.DeadEventHandler;
import com.xyz.cloud.retry.deadevent.InfiniteRetryDeadEventHandler;
import com.xyz.cloud.retry.repository.EventRepository;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfig {
 //   @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(@Value("${spring.redis.address}") String addressList, @Value("${spring.redis.password:unknown}") String pwd) {
        Config config = new Config();
        String[] address = StringUtils.split(addressList, ",");
        if (address.length == 1) {
            config.useSingleServer().setPassword(pwd).setAddress(address[0]);
        } else {
            config.useClusterServers().addNodeAddress(address);
        }
        return Redisson.create(config);
    }

    @Bean
    public DeadEventHandler infiniteRetryDeadEventHandler(EventRepository eventRepositoryTemplate, EventRepositoryFactory eventRepositoryFactory) {
        return new InfiniteRetryDeadEventHandler(eventRepositoryTemplate, eventRepositoryFactory);
    }
}
