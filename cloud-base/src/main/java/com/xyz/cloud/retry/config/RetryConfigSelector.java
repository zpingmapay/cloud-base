package com.xyz.cloud.retry.config;

import com.xyz.cloud.lock.annotation.EnableLock;
import com.xyz.cloud.retry.annotation.EnableRetryableEvent;
import com.xyz.cloud.retry.sotre.EventStore;
import com.xyz.cloud.retry.sotre.RedisEventStore;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Objects;

public class RetryConfigSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(
                        EnableRetryableEvent.class.getName(), false));

        Class<? extends EventStore> providerClass = Objects.requireNonNull(attributes).getClass("store");
        if (providerClass.getName().equals(RedisEventStore.class.getName())) {
            return new String[]{RedisStoreConfiguration.class.getName(), RetryableConfiguration.class.getName()};
        }
        return new String[]{RamStoreConfiguration.class.getName(), RetryableConfiguration.class.getName()};
    }
}
