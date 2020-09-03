package com.xyz.utils;

import com.xyz.exception.ValidationException;

import java.util.Collection;

public class ValidationUtils {
    public static void notBlank(String string, String message) {
        notBlank(string, null, message);
    }

    public static void notBlank(String string, String code, String message) {
        if (string == null || string.trim().length() == 0) {
            throw new ValidationException(code, message);
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        notEmpty(collection, null, message);
    }

    public static void notEmpty(Collection<?> collection, String code, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new ValidationException(message);
        }
    }

    public static void notNull(Object object, String message) {
        notNull(object, null, message);
    }

    public static void notNull(Object object, String code, String message) {
        if (object == null) {
            throw new ValidationException(code, message);
        }
    }

    public static void isTrue(Boolean exp, String message) {
        isTrue(exp, null, message);
    }

    public static void isTrue(boolean exp, String code, String message) {
        if (!exp) {
            throw new ValidationException(code, message);
        }
    }
}
