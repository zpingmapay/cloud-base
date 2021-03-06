package com.xyz.desensitize.annotation;

import com.github.houbb.sensitive.annotation.metadata.SensitiveStrategy;
import com.github.houbb.sensitive.core.api.strategory.StrategyEmail;

import java.lang.annotation.*;

/**
 * 邮箱脱敏
 *
 * @author dev-sxl
 * date 2020-09-14
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@SensitiveStrategy(StrategyEmail.class)
public @interface DesensitizeEmail {
}
