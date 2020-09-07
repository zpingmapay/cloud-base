package com.xyz.sample.client;

import com.xyz.client.feign.interceptor.SapiRequestSigner;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.sample.client.dto.AuthCardDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author sxl
 * @since 2020/7/23 15:40
 */
@FeignClient(name = "sapi-signer", url = "${cloud.client.signer.sapi-signer.url}",
        configuration = {SapiRequestSigner.class})
public interface SapiSignerServiceSpi {

    @PostMapping(value = "/auth/card", headers = "method=auth.card")
    ResultDto<AuthCardDto.Response> authCard(AuthCardDto.Request request);
}
