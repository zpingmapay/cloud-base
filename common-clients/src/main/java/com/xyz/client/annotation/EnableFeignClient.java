package com.xyz.client.annotation;

import com.xyz.client.config.OAuthClientConfig;
import com.xyz.client.feign.config.FeignClientConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author sxl
 * @since 2020/7/30 11:28
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties(OAuthClientConfig.class)
@Import({FeignClientConfiguration.class})
public @interface EnableFeignClient {

}
