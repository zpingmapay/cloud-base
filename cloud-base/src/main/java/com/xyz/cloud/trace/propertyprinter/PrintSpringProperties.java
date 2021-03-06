package com.xyz.cloud.trace.propertyprinter;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({PropertyPrinter.class})
public @interface PrintSpringProperties {
}
