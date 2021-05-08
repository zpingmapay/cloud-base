package com.xyz.cloud.utils;

import lombok.Data;

/**
 * @author lihongbin
 * @date 2021年05月08日 21:07
 */
@Data
public class TestTemplate {
    private String money;

    private SubClass subClass;

    @Data
    public static class SubClass {

        public Integer num;
    }
}
