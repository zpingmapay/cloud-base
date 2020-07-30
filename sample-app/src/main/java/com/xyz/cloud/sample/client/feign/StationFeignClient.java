package com.xyz.cloud.sample.client.feign;

import com.xyz.client.FeignFormSupportConfig;
import com.xyz.client.FeignPhpSignConfig;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.cloud.sample.client.feign.dto.DiyPriceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author sxl
 * @since 2020/7/23 15:40
 */
@FeignClient(name = "open-data", url = "https://open-data.51zhaoyou.com/test",
        configuration = {FeignFormSupportConfig.class, FeignPhpSignConfig.class})
@RequestMapping(headers = {
        FeignPhpSignConfig.HEADER_APP_ID_PREFIX + "${cloud.openapi.app-id}",
        FeignPhpSignConfig.HEADER_APP_KEY_PREFIX + "${cloud.openapi.app-key}",
})
public interface StationFeignClient {

    /**
     * 站点调价
     *
     * @param request 请求参数
     * @return 调价结果
     */
    @PostMapping(value = "/", headers = "method=station.setDiyPrice", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResultDto<DiyPriceDto.Response> setStationDiyPrice(DiyPriceDto.Request request);

}
