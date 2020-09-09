package com.xyz.lombokplus;

import com.xyz.utils.JsonUtils;
import lombok.Convertable;
import lombok.Data;
import lombok.JsonSerializable;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.Date;

public class ConvertableTest {
    @Test
    public void should_all_success() {
        Sample sample = initSample();
        Pojo converted = sample.toBean();
        Assert.isTrue(sample.getParam1().equals(converted.getParam1()), "convert failed");
        sample = Sample.fromBean(converted);
        Assert.isTrue(sample.getParam1().equals(converted.getParam1()), "convert failed");

        Sample1 sample1 = JsonUtils.convert(sample, Sample1.class);
        converted = sample1.toBean(Pojo.class);
        Assert.isTrue(sample.getParam1().equals(converted.getParam1()), "convert failed");
        sample1 = Sample1.fromBean(converted);
        Assert.isTrue(sample.getParam1().equals(converted.getParam1()), "convert failed");
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
    public static class Pojo {
        private String param1;
        private Date param2;
        private long param3;
        private boolean param4;
        private BigDecimal param5;

    }

    @Data
    @JsonSerializable
    @Convertable(bean = Pojo.class)
    public static class Sample {
        private String param1;
        private Date param2;
        private long param3;
        private boolean param4;
        private BigDecimal param5;
    }

    @Data
    @JsonSerializable
    @Convertable
    public static class Sample1 {
        private String param1;
        private Date param2;
        private long param3;
        private boolean param4;
        private BigDecimal param5;
    }
}
