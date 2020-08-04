package com.xyz.cloud.sample.client.feign;

import com.xyz.client.feign.interceptor.RequestSigner;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.cloud.sample.client.feign.config.FeignFormSupportConfig;
import com.xyz.cloud.sample.client.feign.dto.DiyPriceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author sxl
 * @since 2020/7/23 15:40
 */
@FeignClient(name = "remote-service-3", url = "${cloud.client.remote-service-3.url}",
        configuration = {FeignFormSupportConfig.class, RequestSigner.class})
@RequestMapping(headers = {
        RequestSigner.HEADER_APP_ID_PREFIX + "${cloud.client.remote-service-3.app-id}",
        RequestSigner.HEADER_APP_KEY_PREFIX + "${cloud.client.remote-service-3.app-key}",
})
public interface StationFeignClient {
    @PostMapping(value = "/", headers = "method=station.setDiyPrice", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResultDto<DiyPriceDto.Response> setStationDiyPrice(DiyPriceDto.Request request);

}
