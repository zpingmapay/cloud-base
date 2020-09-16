package com.xyz.desensitize.annotation;

import com.github.houbb.sensitive.annotation.metadata.SensitiveStrategy;
import com.github.houbb.sensitive.core.api.strategory.StrategyChineseName;

import java.lang.annotation.*;

/**
 * 中文名字脱敏
 *
 * @author dev-sxl
 * date 2020-09-14
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@SensitiveStrategy(StrategyChineseName.class)
public @interface DesensitizeChineseName {
}
