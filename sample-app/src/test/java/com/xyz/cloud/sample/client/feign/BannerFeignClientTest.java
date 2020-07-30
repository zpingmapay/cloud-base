package com.xyz.cloud.sample.client.feign;

import com.alibaba.fastjson.JSON;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.cloud.sample.ApplicationTests;
import com.xyz.cloud.sample.client.feign.dto.BannerQueryVo;
import com.xyz.cloud.sample.client.feign.dto.PageVo;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * @author sxl
 * @since 2020/7/24 14:56
 */
class BannerFeignClientTest extends ApplicationTests {

    @Resource
    private BannerFeignClient bannerFeignClient;

    @Test
    void bannerActivityQuery() {
        BannerQueryVo.Request request = new BannerQueryVo.Request();
        request.setAppId("1");
        for (int i = 0; i < 3; i++) {
            ResultDto<PageVo<BannerQueryVo.Response>> resultDto = bannerFeignClient.bannerActivityQuery(request);
            System.out.println("method: BannerFeignClientTest.bannerActivityQuery, param: resultDto= " + JSON.toJSONString(resultDto));
        }
    }

}