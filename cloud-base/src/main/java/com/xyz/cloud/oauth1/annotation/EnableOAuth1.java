package com.xyz.cloud.oauth1.annotation;

import com.xyz.cloud.oauth1.OAuth1Configuration;
import com.xyz.cloud.oauth1.OAuthServerConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties(OAuthServerConfig.class)
@Import({OAuth1Configuration.class})
public @interface EnableOAuth1 {
}
