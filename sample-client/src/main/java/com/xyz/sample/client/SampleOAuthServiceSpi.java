package com.xyz.sample.client;

import com.xyz.client.feign.interceptor.OAuth1Interceptor;
import com.xyz.cloud.dto.ResultDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "sample-oauth", url = "${cloud.client.oauth.sample-oauth.url}"
        ,configuration = {OAuth1Interceptor.class}
)
public interface SampleOAuthServiceSpi {
    @GetMapping("/me")
    ResultDto<String> myUserId(@RequestParam String userName);
}
