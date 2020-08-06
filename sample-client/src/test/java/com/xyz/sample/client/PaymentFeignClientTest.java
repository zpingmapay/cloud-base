package com.xyz.sample.client;

import com.alibaba.fastjson.JSON;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.sample.client.dto.ChannelGetDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sxl
 * @since 2020/7/24 10:16
 */
@SpringBootTest
public class PaymentFeignClientTest {
    @Resource
    private PaymentFeignClient paymentFeignClient;

    @Test
    void mchChannelGet() {
        ChannelGetDto.ReqDto reqDto = new ChannelGetDto.ReqDto();
        reqDto.setMchId(201908127100L);
        ResultDto<List<ChannelGetDto.RespDto>> resultDto = paymentFeignClient.mchChannelGet(reqDto);
        System.out.println("method: PaymentFeignClientTest.mchChannelGet, param: resultDto= " + JSON.toJSONString(resultDto));
    }
}