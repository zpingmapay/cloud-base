package com.xyz.client.annotation;

import com.xyz.client.config.FeignLoggerAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author sxl
 * @since 2020/7/30 11:28
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({FeignLoggerAutoConfiguration.class})
public @interface EnableFeignLogger {

}
