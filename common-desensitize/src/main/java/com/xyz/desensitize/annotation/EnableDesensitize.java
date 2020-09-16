package com.xyz.desensitize.annotation;

import com.xyz.desensitize.config.DesensitizeConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 脱敏开关
 *
 * @author dev-sxl
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({DesensitizeConfiguration.class})
public @interface EnableDesensitize {
}