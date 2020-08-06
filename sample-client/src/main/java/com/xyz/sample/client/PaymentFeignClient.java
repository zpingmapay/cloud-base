package com.xyz.sample.client;

import com.xyz.cloud.dto.ResultDto;
import com.xyz.sample.client.dto.ChannelGetDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * @author sxl
 * @since 2020/7/23 15:40
 */
@FeignClient(name = "remote-service-3", url = "${cloud.client.remote-service-3.url}")
public interface PaymentFeignClient {
    @PostMapping(value = "/channel/mchChannelGet")
    ResultDto<List<ChannelGetDto.RespDto>> mchChannelGet(ChannelGetDto.ReqDto reqDto);

}
