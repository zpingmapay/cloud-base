package com.xyz.utils;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import static com.xyz.utils.BeanUtilsTest.initSample;

public class JsonUtilsTest {
    @Test
    public void should_all_success() {
        BeanUtilsTest.Sample sample = initSample();
        String json = JsonUtils.beanToJson(sample);
        Assert.notNull(json, "Bean to json failed");

        BeanUtilsTest.Sample sample1 = JsonUtils.jsonToBean(json, BeanUtilsTest.Sample.class);
        Assert.isTrue(sample1.getParam5().equals(sample.getParam5()), "Json to bean failed");

    }
}
