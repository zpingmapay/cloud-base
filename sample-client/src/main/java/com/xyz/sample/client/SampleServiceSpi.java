package com.xyz.sample.client;

import com.xyz.client.feign.interceptor.OAuth1FeignClient;
import com.xyz.cloud.dto.ResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "sample", url = "${cloud.client.oauth.sample.url}"
        ,configuration = {OAuth1FeignClient.class}
)
public interface SampleServiceSpi {
    @GetMapping("/me")
    ResultDto<String> myUserId();
}
