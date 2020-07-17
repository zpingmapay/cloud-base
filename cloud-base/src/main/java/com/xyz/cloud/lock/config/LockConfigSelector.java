package com.xyz.cloud.lock.config;

import com.xyz.cloud.lock.annotation.EnableLock;
import com.xyz.cloud.lock.provider.LockProvider;
import com.xyz.cloud.lock.provider.RedisLockProvider;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Objects;

public class LockConfigSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                annotationMetadata.getAnnotationAttributes(
                        EnableLock.class.getName(), false));

        Class<? extends LockProvider> providerClass = Objects.requireNonNull(attributes).getClass("provider");
        if(providerClass.getName().equals(RedisLockProvider.class.getName())) {
            return new String[]{RedisLockConfiguration.class.getName()};
        }
        return new String[]{RamLockConfiguration.class.getName()};
    }
}
