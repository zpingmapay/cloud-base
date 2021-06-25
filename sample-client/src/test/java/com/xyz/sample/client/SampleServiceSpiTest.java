package com.xyz.sample.client;

import com.xyz.cloud.dto.ResultDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;

/**
 * @author sxl
 * @since 2020/7/24 10:16
 */
@SpringBootTest
@Disabled
public class SampleServiceSpiTest {
    @Resource
    private SampleServiceSpi sampleServiceSpi;

    @Test
    void testLogin() {
        ResultDto<String> result = sampleServiceSpi.login();
        Assert.isTrue(result.resultOk(), "login failed");
    }
}