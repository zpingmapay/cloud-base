package com.xyz.lombokplus;

import lombok.WithCodeAndDesc;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;


public class WithCodeAndDescTest {
    @Test
    public void testWithCodeAndDesc() {
        Assert.isTrue("test".equals(SampleEnum.of(2).getDesc()), "desc incorrect");
    }

    @WithCodeAndDesc
    public enum SampleEnum {
        SampleEnum1(1), SampleEnum2(2, "test");
    }
}
