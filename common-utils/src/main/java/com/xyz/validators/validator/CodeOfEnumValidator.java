package com.xyz.validators.validator;

import com.xyz.validators.annotation.CodeOfEnum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author sxl
 * @since 2020/8/24 15:41
 */
public class CodeOfEnumValidator implements ConstraintValidator<CodeOfEnum, Object> {

    private Class<? extends Enum<?>> enumClass;
    private String codeFieldName;

    /**
     * Initializes the validator in preparation for
     * {@link #isValid(Object, ConstraintValidatorContext)} calls.
     * The constraint annotation for a given constraint declaration
     * is passed.
     * <p>
     * This method is guaranteed to be called before any use of this instance for
     * validation.
     * <p>
     * The default implementation is a no-op.
     *
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(CodeOfEnum constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
        this.codeFieldName = constraintAnnotation.codeFieldName();
    }

    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     * <p>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        try {
            Field field = enumClass.getDeclaredField(codeFieldName);
            field.setAccessible(true);
            for (Enum<?> anEnum : enumClass.getEnumConstants()) {
                if (Objects.equals(value, field.get(anEnum))) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return false;
    }

}
