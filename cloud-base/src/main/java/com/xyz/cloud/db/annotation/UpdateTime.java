package com.xyz.cloud.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 时间注解，在实体类对应字段添加注解，更新操作时会自动添加时间
 *
 * @author sxl
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface UpdateTime {

    String value() default "";
}