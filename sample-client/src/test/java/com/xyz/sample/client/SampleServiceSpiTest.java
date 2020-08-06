package com.xyz.sample.client;

import com.xyz.cloud.dto.ResultDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;

@SpringBootTest
public class SampleServiceSpiTest {
    @Resource
    private SampleServiceSpi sampleServiceSpi;

    @Test
    public void testSampleService() {
        ResultDto<String> result = sampleServiceSpi.myUserId();
        Assert.isTrue(result.resultOk(), "failed to get my user id");
    }
}
