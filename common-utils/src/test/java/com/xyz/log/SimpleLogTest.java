package com.xyz.log;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class SimpleLogTest {
    @Test
    public void test() {
        SimpleLog logger = new SimpleLog("com.zhaoyou");
        String template = "This is a {}, don't worry about it, {}. see you";
        logger.info(log, template, "test", "Peter");
        logger.error(log, template, new RuntimeException("exp"), "test", "Exception");
    }
}
