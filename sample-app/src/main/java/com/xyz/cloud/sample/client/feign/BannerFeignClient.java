package com.xyz.cloud.sample.client.feign;

import com.xyz.client.feign.interceptor.OAuth1Client;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.cloud.sample.client.feign.dto.BannerQueryVo;
import com.xyz.cloud.sample.client.feign.dto.PageVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


/**
 * @author sxl
 * @since 2020/7/23 15:40
 */
@FeignClient(name = "sample1", url = "${cloud.client.remote-service-1.url}"
        ,configuration = {OAuth1Client.class}
)
@RequestMapping(
//        headers = {
//                "consumer-key=${cloud.client.remote-service-1.consumer-key}",
//                "consumer-secret=${cloud.client.remote-service-1.consumer-secret}"
//        }
)
public interface BannerFeignClient {
    @PostMapping(value = "api/v1/banner/query")
    ResultDto<PageVo<BannerQueryVo.Response>> bannerActivityQuery(BannerQueryVo.Request param);

}
