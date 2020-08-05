package com.xyz.cloud.sample.client.feign;

import com.xyz.client.feign.interceptor.SimpleRequestSigner;
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
@FeignClient(name = "remote-service-3", url = "${cloud.client.signer.remote-service-3.url}",
        configuration = {FeignFormSupportConfig.class, SimpleRequestSigner.class})
@RequestMapping(headers = {
        SimpleRequestSigner.HEADER_APP_ID_PREFIX + "${cloud.client.remote-service-3.app-id}",
        SimpleRequestSigner.HEADER_APP_KEY_PREFIX + "${cloud.client.remote-service-3.app-key}",
})
public interface StationFeignClient {
    @PostMapping(value = "/", headers = "method=station.setDiyPrice", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResultDto<DiyPriceDto.Response> setStationDiyPrice(DiyPriceDto.Request request);

}
