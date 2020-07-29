package com.xyz.cloud.trace.annotation;

import com.xyz.cloud.trace.config.TraceableConfigSelector;
import com.xyz.cloud.trace.holder.DomainHeadersHolder;
import com.xyz.cloud.trace.holder.HttpHeadersHolder;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({TraceableConfigSelector.class})
public @interface EnableTraceable {
    Class<? extends HttpHeadersHolder> holder() default DomainHeadersHolder.class;
}
