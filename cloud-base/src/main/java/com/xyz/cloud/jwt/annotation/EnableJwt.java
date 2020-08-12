package com.xyz.cloud.jwt.annotation;

import com.xyz.cloud.jwt.JwtConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({JwtConfiguration.class})
public @interface EnableJwt {
}
