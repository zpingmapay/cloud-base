package com.xyz.validators.validator;

import com.xyz.validators.annotation.CodeOfEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author sxl
 * @since 2020/8/26 09:33
 */
class CodeOfEnumValidatorTest {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    public void testLegalCodeOfEnum() {
        User user = new User();
        user.setName("user1");
        user.setUserType(1);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assert.isTrue(CollectionUtils.isEmpty(violations), "legal code validation fail");
    }

    @Test
    public void testIllegalCodeOfEnum() {
        User user = new User();
        user.setName("user2");
        user.setUserType(0);

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assert.isTrue(CollectionUtils.isNotEmpty(violations), "illegal code validation fail");
        Assert.isTrue(violations.stream().anyMatch(validation -> "user type error".equals(validation.getMessage()))
                , "illegal code validation fail");
    }

    @Data
    public static class User {

        private String name;

        @CodeOfEnum(enumClass = UserType.class, codeFieldName = "type", message = "user type error")
        private Integer userType;
    }

    @Getter
    @AllArgsConstructor
    public enum UserType {

        GENERAL_USER(1, "general user"),
        COMPANY_USER(2, "company user"),
        ;

        private final Integer type;
        private final String desc;
    }

}