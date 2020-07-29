package com.xyz.cloud.trace.config;

import com.xyz.cloud.trace.annotation.EnableTraceable;
import com.xyz.cloud.trace.holder.DomainHeadersHolder;
import com.xyz.cloud.trace.holder.HttpHeadersHolder;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Objects;

public class TraceableConfigSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(
                        EnableTraceable.class.getName(), false));

        Class<? extends HttpHeadersHolder> holderClass = Objects.requireNonNull(attributes).getClass("holder");
        if(holderClass.getName().equals(DomainHeadersHolder.class.getName())) {
            return new String[]{TraceableThreadPoolConfiguration.class.getName(), TraceableConfiguration.class.getName()};
        }
        return new String[]{TraceableThreadPoolConfiguration.class.getName(), DefaultTraceableConfiguration.class.getName()};
    }
}
