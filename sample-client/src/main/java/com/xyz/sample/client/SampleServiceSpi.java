package com.xyz.sample.client;

import com.xyz.cloud.dto.ResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author sxl
 * @since 2020/7/23 15:40
 */
@FeignClient(name = "sample", url = "${cloud.client.sample.url}")
public interface SampleServiceSpi {
    @PostMapping(value = "/login")
    ResultDto<String> login();

}
