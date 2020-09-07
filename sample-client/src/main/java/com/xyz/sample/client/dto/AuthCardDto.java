package com.xyz.sample.client.dto;

import lombok.Data;

/**
 * @author sxl
 * @since 2020/9/7 15:11
 */
public class AuthCardDto {

    @Data
    public static class Request {
        private String stationId;
        private String gunCode;
        private String skuCode;
        private String code;
        private String tradeSn;
    }

    @Data
    public static class Response {
    }
}
