package com.xyz.cloud.trace.propertyprinter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.util.Arrays;
import java.util.stream.StreamSupport;

@Slf4j
public class PropertyPrinter {
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        final Environment env = event.getApplicationContext().getEnvironment();
        log.info("============ Spring properties ============");
        log.info("Active profiles: {}", Arrays.toString(env.getActiveProfiles()));
        final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();

        StreamSupport.stream(sources.spliterator(), false)
                .filter(ps -> ps instanceof MapPropertySource && !ps.getName().contains("system"))
                .map(ps -> ((MapPropertySource) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .distinct()
                .filter(prop -> !isSensitive(prop))
                .forEach(prop -> log.info("{}: {}", prop, env.getProperty(prop)));
        log.info("===========================================");
    }

    private static final Iterable<String> sensitiveKeys = Arrays.asList("secret", "credential", "password");

    boolean isSensitive(String prop) {
        return StreamSupport.stream(sensitiveKeys.spliterator(), false).anyMatch(prop::contains);
    }
}
