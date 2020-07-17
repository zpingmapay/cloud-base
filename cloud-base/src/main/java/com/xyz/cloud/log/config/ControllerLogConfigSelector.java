package com.xyz.cloud.log.config;

import com.xyz.cloud.log.annotation.EnableControllerLog;
import com.xyz.cloud.log.holder.DomainHeadersHolder;
import com.xyz.cloud.log.holder.HttpHeadersHolder;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Objects;

public class ControllerLogConfigSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(
                        EnableControllerLog.class.getName(), false));

        Class<? extends HttpHeadersHolder> holderClass = Objects.requireNonNull(attributes).getClass("holder");
        if(holderClass.getName().equals(DomainHeadersHolder.class.getName())) {
            return new String[]{DomainControllerLogConfiguration.class.getName()};
        }
        return new String[]{DefaultControllerLogConfiguration.class.getName()};
    }
}
