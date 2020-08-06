package com.xyz.sample.client;

import com.alibaba.fastjson.JSON;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.sample.client.dto.BannerQueryVo;
import com.xyz.sample.client.dto.PageVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author sxl
 * @since 2020/7/24 14:56
 */
@SpringBootTest
public class BannerFeignClientTest {
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