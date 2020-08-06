package com.xyz.sample.client;

import com.xyz.cloud.dto.ResultDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;

@SpringBootTest
public class SampleOAuthServiceSpiTest {
    @Resource
    private SampleOAuthServiceSpi SampleOAuthServiceSpi;

    @Test
    public void testMyUserId() {
        ResultDto<String> result = SampleOAuthServiceSpi.myUserId();
        Assert.isTrue(result.resultOk(), "failed to get my user id");
    }
}
