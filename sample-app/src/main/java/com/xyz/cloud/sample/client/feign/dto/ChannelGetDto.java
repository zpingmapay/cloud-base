package com.xyz.cloud.sample.client.feign.dto;

import lombok.Data;


public abstract class ChannelGetDto {

    @Data
    public static class ReqDto {

        /** 渠道id */
        private Long id;
        /** 支付服务商户id */
        private Long mchId;
        /** 支付渠道注册的找油商家编号 */
        private String mchCode;
        /** 渠道归属商户id */
        private Long belongMchId;
        /**
         * 默认审核通过，避免job，当新添加的商户需要审核时，
         * 渠道审核状态，0、审核中 1、待签署电子协议 2、审核通过 3、审核驳回
         */
        private Integer auditStatus;
        /** 渠道类型 */
        private String channelType;
    }

    @Data
    public static class RespDto {

        private Long id;
        private String appId;//渠道app_id
        private String mchCode;//支付渠道注册的找油商家编号
        private String channelName;//备案公司
        private String privateKey;//渠道私钥
        private String publicKey;//渠道公钥
        private String payType;//支付类型
        private String payTypeDesc;//支付类型
        /**
         * 0 注册 1、审核中 2、审核通过 3、审核驳回 4 已签署电子协议
         */
        private Integer channelStatus;

        private String channelStatusDesc;
        /**
         * 渠道归属 归属的商户的商户表id
         */
        private Long belongMchId;
        /**
         * 终端号
         */
        private String terminalId;
        /**
         * 渠道类型
         */
        private String channelType;
        private String channelTypeDesc;
        /**
         * 渠道账户id
         */
        private String accountId;
    }

}
