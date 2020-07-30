package com.xyz.cloud.sample.client.feign.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author : sxl
 * @date : 2019/8/7 10:12
 */
public class DiyPriceDto {

    @Data
    public static class Request {
        /**
         * 站点id
         */
        private Long stationId;
        /**
         * 油品(如：D100G06)
         */
        private String skuCode;

        /**
         * 设置类型1按公司 2按应用（默认）
         */
        private Integer setType;
        /**
         * setType为1是传公司ID, 为2时传应用ID
         */
        private Long relId;
        /**
         * 调价方式,11发改委价优惠 12发改委价折扣 21枪标价优惠 22枪标价折扣 40油站结算价优惠 43油站结算价折扣 41手动定价
         */
        private Integer setModel;
        /**
         * 调价对应值,调价值：正负表示加减，（如：便宜2毛为-20，95折为-5不需要百分号）， 41=固定值（单位：分）
         */
        private Integer setVal;

        /**
         * 生效时间（如：2020-04-30 11:28:30），默认当前时间
         */
        private String startTime;
    }

    @Data
    public static class Response {
        private Long logId;
        /**
         * 调整后的价格
         */
        private BigDecimal diyPrice;
    }

}
