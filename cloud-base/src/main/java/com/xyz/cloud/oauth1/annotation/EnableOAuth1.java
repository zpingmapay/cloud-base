package com.xyz.cloud.oauth1.annotation;

import com.xyz.cloud.oauth1.OAuth1Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({OAuth1Configuration.class})
public @interface EnableOAuth1 {
}
