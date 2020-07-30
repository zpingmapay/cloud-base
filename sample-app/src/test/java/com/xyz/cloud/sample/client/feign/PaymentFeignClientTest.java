package com.xyz.cloud.sample.client.feign;

import com.alibaba.fastjson.JSON;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.cloud.sample.ApplicationTests;
import com.xyz.cloud.sample.client.feign.dto.ChannelGetDto;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sxl
 * @since 2020/7/24 10:16
 */
class PaymentFeignClientTest extends ApplicationTests {

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