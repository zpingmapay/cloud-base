package com.xyz.desensitize.annotation;

import java.lang.annotation.*;

/**
 * 脱敏注解,添加到方法上代表对方法返回结果进行脱敏
 *
 * @author sxl
 * @since 2020/9/15 11:52
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Desensitize {
}
