package com.xyz.sample.client;

import com.xyz.client.feign.interceptor.SimpleRequestSigner;
import com.xyz.client.feign.config.FeignFormSupportConfig;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.sample.client.dto.DiyPriceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author sxl
 * @since 2020/7/23 15:40
 */
@FeignClient(name = "sample-signer", url = "${cloud.client.signer.sample-signer.url}",
        configuration = {FeignFormSupportConfig.class, SimpleRequestSigner.class})
public interface SampleSignerServiceSpi {

    @PostMapping(value = "/", headers = "method=station.setDiyPrice", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResultDto<DiyPriceDto.Response> setStationDiyPrice(DiyPriceDto.Request request);
}
