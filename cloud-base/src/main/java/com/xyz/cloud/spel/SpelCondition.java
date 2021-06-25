package com.xyz.cloud.spel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.xyz.cloud.spel.SpelRelation.EQ;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SpelCondition {
    String name() default "";

    SpelRelation relation() default EQ;

    String msg() default "";
}
