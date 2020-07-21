package com.xyz.utils;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

public class IdGeneratorTest {
    @Test
    public void testRandomDigits() {
        String digits = IdGenerator.randomDigits(10);
        Assert.isTrue(digits.length() == 10, "len is not 10");
        Long longVal = Long.valueOf(digits);
        Assert.isTrue(longVal > 0, "long value < 0");
    }

    @Test
    public void testRandomChars() {
        String digits = IdGenerator.randomChars(512);
        Assert.isTrue(digits.length() == 512, "len is not 512");
    }
}
