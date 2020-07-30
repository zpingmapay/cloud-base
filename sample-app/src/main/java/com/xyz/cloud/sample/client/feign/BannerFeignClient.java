package com.xyz.cloud.sample.client.feign;

import com.xyz.client.FeignOauthClient;
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
@FeignClient(name = "promotion-service", url = "https://promotion-service.51zhaoyou.com/test",
        configuration = {FeignOauthClient.class})
@RequestMapping(headers = {
        "app-id=4",
        "consumer-key=Zpetrolservice51serviceY",
        "consumer-secret=215419DCDF9E9F5160545E517624FEBB"
})
public interface BannerFeignClient {

    /**
     * 商户支付渠道信息接口
     *
     * @param param 参数
     * @return 商户支付渠道信息
     */
    @PostMapping(value = "api/v1/banner/query")
    ResultDto<PageVo<BannerQueryVo.Response>> bannerActivityQuery(BannerQueryVo.Request param);

}
