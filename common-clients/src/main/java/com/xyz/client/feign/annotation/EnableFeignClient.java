package com.xyz.client.feign.annotation;

import com.xyz.client.feign.config.FeignClientConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author sxl
 * @since 2020/7/30 11:28
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({FeignClientConfiguration.class})
public @interface EnableFeignClient {

}
