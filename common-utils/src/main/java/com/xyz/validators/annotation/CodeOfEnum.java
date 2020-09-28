package com.xyz.validators.annotation;

import com.xyz.validators.validator.CodeOfEnumValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * code of enum validator
 *
 * @author sxl
 * @since 2020/8/24 15:40
 */
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = CodeOfEnumValidator.class)
public @interface CodeOfEnum {

    Class<? extends Enum<?>> enumClass();

    String codeFieldName() default "code";

    String message() default "must be any of enum {enumClass}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
