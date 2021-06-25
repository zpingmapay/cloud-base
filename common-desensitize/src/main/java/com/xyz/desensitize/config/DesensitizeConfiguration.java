package com.xyz.desensitize.config;

import com.xyz.desensitize.aspects.DesensitizeAspect;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sxl
 * @since 2020/9/15 11:44
 */
@Slf4j
@Configuration
public class DesensitizeConfiguration {

    @Bean
    public DesensitizeAspect desensitizeAspect() {
        return new DesensitizeAspect();
    }

}