package com.xyz.cloud.retry.config;

import com.xyz.cloud.retry.sotre.RamEventStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Slf4j
public class RamStoreConfiguration {
    @Bean("RamEventStore")
    public RamEventStore ramEventStore() {
        return new RamEventStore();
    }
}
