package com.xyz.cloud.log.annotation;

import com.xyz.cloud.log.config.ControllerLogConfigSelector;
import com.xyz.cloud.log.holder.DomainHeadersHolder;
import com.xyz.cloud.log.holder.HttpHeadersHolder;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ControllerLogConfigSelector.class})
public @interface EnableControllerLog {
    Class<? extends HttpHeadersHolder> holder() default DomainHeadersHolder.class;
}
