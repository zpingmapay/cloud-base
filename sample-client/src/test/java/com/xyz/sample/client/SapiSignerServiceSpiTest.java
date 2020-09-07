package com.xyz.sample.client;

import com.alibaba.fastjson.JSON;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.sample.client.dto.AuthCardDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author sxl
 * @since 2020/5/13 16:42
 */
@SpringBootTest
@Disabled
public class SapiSignerServiceSpiTest {
    @Resource
    private SapiSignerServiceSpi sampleSignerServiceSpi;

    @Test
    void authCard() {
        AuthCardDto.Request request = new AuthCardDto.Request();
        request.setStationId("165481003");
        request.setSkuCode("5");
        request.setGunCode("16548100302");
        request.setCode("EGC_粤AB11112&15010217901050&测试司机2xf&13600000001");
        request.setTradeSn("Z190905101350882352");

        ResultDto<AuthCardDto.Response> resultDto = sampleSignerServiceSpi.authCard(request);
        System.out.println("method: SampleSigner1ServiceSpiTest.setStationDiyPrice, param: resultDto= " + JSON.toJSONString(resultDto));
    }

}