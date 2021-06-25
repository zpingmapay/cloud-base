package com.xyz.desensitize.annotation;

import com.github.houbb.sensitive.annotation.metadata.SensitiveStrategy;
import com.xyz.desensitize.strategy.CustomPasswordStrategy;

import java.lang.annotation.*;

/**
 * 密码脱敏
 *
 * @author dev-sxl
 * date 2020-09-14
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@SensitiveStrategy(CustomPasswordStrategy.class)
public @interface DesensitizePassword {
}
