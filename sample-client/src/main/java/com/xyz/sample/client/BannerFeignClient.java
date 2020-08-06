package com.xyz.sample.client;

import com.xyz.client.feign.interceptor.OAuth1FeignClient;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.sample.client.dto.BannerQueryVo;
import com.xyz.sample.client.dto.PageVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;


/**
 * @author sxl
 * @since 2020/7/23 15:40
 */
@FeignClient(name = "sample1", url = "${cloud.client.oauth.remote-service-1.url}"
        ,configuration = {OAuth1FeignClient.class}
)
public interface BannerFeignClient {
    @PostMapping(value = "api/v1/banner/query")
    ResultDto<PageVo<BannerQueryVo.Response>> bannerActivityQuery(BannerQueryVo.Request param);

}
