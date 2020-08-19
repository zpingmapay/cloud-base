package com.xyz.utils;

import com.xyz.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.fail;

public class ValidationUtilsTest {
    @Test
    public void should_all_success() {
        ValidationUtils.isTrue(1 == 1, "Is true failed");
        ValidationUtils.notBlank("not blank", "Not blank failed");
        ValidationUtils.notNull("not null", "Not null failed");
        assertException(x -> ValidationUtils.notEmpty(null, "Not empty failed"), ValidationException.class);
    }

    public static <T> void assertException(Consumer<Void> consumer, Class<T> exceptionClass) {
        try {
            consumer.accept(null);
            fail("Expected exception to be thrown here");
        } catch (Exception e) {
            Assert.isTrue(e.getClass().equals(exceptionClass), "Exception expected here!");
        }
    }
}
