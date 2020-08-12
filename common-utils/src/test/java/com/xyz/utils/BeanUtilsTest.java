package com.xyz.utils;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class BeanUtilsTest {
    @Test
    public void should_all_success() {
        Sample sample = initSample();
        Map<String, Object> map = BeanUtils.beanToMap(sample);
        Assert.isTrue(map.containsKey("param1"), "Bean to map failed");

        Sample sample1 = BeanUtils.mapToBean(map, Sample.class);
        Assert.isTrue(sample1.getParam5().equals(sample.getParam5()), "Map to bean failed");
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
    public static class Sample {
        private String param1;
        private Date param2;
        private long param3;
        private boolean param4;
        private BigDecimal param5;
    }
}
