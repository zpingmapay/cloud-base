package com.xyz.utils;

import com.xyz.exception.ValidationException;
import org.junit.jupiter.api.Test;

public class ValidationUtilsTest {
    @Test
    public void should_all_success() {
        ValidationUtils.isTrue(1 == 1, "Is true failed");
        ValidationUtils.notBlank("not blank", "Not blank failed");
        ValidationUtils.notNull("not null", "Not null failed");
        ValidationUtils.assertException(x -> ValidationUtils.notEmpty(null, "Not empty failed"), ValidationException.class);
    }
}
