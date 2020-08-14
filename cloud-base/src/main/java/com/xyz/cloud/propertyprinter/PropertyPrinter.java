package com.xyz.cloud.propertyprinter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.*;

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
                .filter(prop -> !(prop.contains("credentials") || prop.contains("password") || prop.contains("secret")))
                .forEach(prop -> log.info("{}: {}", prop, env.getProperty(prop)));
        log.info("===========================================");
    }
}
