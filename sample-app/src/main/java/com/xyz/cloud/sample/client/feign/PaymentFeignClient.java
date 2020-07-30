package com.xyz.cloud.sample.client.feign;

import com.xyz.cloud.dto.ResultDto;
import com.xyz.cloud.sample.client.feign.dto.ChannelGetDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @author sxl
 * @since 2020/7/23 15:40
 */
@FeignClient(name = "payment-service", url = "http://test.pay-trade.51zhaoyou.com")
public interface PaymentFeignClient {

    /**
     * 商户支付渠道信息接口
     *
     * @param reqDto 参数
     * @return 商户支付渠道信息
     */
    @PostMapping(value = "/channel/mchChannelGet")
    ResultDto<List<ChannelGetDto.RespDto>> mchChannelGet(ChannelGetDto.ReqDto reqDto);

}
