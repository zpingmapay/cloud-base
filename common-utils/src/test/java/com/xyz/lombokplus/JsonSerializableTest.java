package com.xyz.lombokplus;

import com.xyz.utils.BeanUtilsTest;
import lombok.Data;
import lombok.JsonSerializable;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;


public class JsonSerializableTest {
    @Test
    public void should_all_success() {
        Sample sample = initSample();
        String json = sample.toJson();

        Sample converted = Sample.fromJson(json);
        Assert.isTrue(converted.getParam1().equals(sample.getParam1()), "convert failed");
    }

    public static Sample initSample() {
        Sample sample = new Sample();
        sample.setParam1("param1");
        sample.setParam2(new Date());
        sample.setParam3(1);
        sample.setParam4(false);
        sample.setParam5(BigDecimal.ONE);
        return sample;
    }

    @Data
    @JsonSerializable
    public static class Sample {
        private String param1;
        private Date param2;
        private long param3;
        private boolean param4;
        private BigDecimal param5;
    }
}
