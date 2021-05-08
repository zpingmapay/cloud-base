package com.xyz.cloud.utils;

import org.junit.jupiter.api.Test;
/**
 * @author lihongbin
 * @date 2021年05月08日 21:19
 */
class TemplateStatementTest {

    @Test
    void simpleTxtProcess() {
        TestTemplate testTemplate = new TestTemplate();
        testTemplate.setMoney("500");
        String s = TemplateStatement.txtProcess("满[(${money})]元可用", testTemplate);
        System.out.println(s);
        assert "满500元可用".equals(s);
    }

    @Test
    void txtProcess() {
        TestTemplate testTemplate = new TestTemplate();
        TestTemplate.SubClass subClass = new TestTemplate.SubClass();
        subClass.setNum(6);
        testTemplate.setSubClass(subClass);
        String s = TemplateStatement.txtProcess("每人限领[(${subClass.num})]张", testTemplate);
        System.out.println(s);
        assert "每人限领6张".equals(s);
    }
}