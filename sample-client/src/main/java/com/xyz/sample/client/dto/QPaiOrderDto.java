package com.xyz.sample.client.dto;

import lombok.Data;

import java.util.List;

/**
 * @author lihongbin
 * 壳牌order dto
 */

public class QPaiOrderDto {
    @Data
    public static class Request {
        private String orderAmount;

        private String paymentAmount;

        private String orderSn;

        private String ossId;

        private String skuCode;
        /**
         * 枪 ID
         */
        private String gunCode;

        private String telSn;
    }

    @Data
    public static class Response {
        private String thirdOrderSn; //找油订单号
        private String bizType; //外部订单编号
        private List<Item> data;
    }

    @Data
    public static class Item {
        private String status;
        private String url;
    }
}
