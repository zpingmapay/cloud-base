package com.xyz.cloud.sample.client.feign;

import com.alibaba.fastjson.JSON;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.cloud.sample.ApplicationTests;
import com.xyz.cloud.sample.client.feign.dto.DiyPriceDto;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;

/**
 * @author sxl
 * @since 2020/5/13 16:42
 */
class StationClientTest extends ApplicationTests {

    @Resource
    private StationFeignClient stationFeignClient;

    @Test
    void setStationDiyPrice() {
        DiyPriceDto.Request request = new DiyPriceDto.Request();
        request.setStationId(3254L);
        request.setSkuCode("D100G06");
        request.setSetType(2);
        request.setRelId(2824L);
        request.setSetModel(21);
        request.setSetVal(-20);
        //request.setStartTime();
        //
        //ResultDto<DiyPriceDto.Response> resultDto = stationClient.setStationDiyPrice(request);
        //System.out.println("method: StationClientTest.setStationDiyPrice, param: resultDto= " + JSON.toJSONString(resultDto));

        ResultDto<DiyPriceDto.Response> resultDto1 = stationFeignClient.setStationDiyPrice(request);
        System.out.println("method: StationClientTest.setStationDiyPrice, param: resultDto1= " + JSON.toJSONString(resultDto1));
    }

}