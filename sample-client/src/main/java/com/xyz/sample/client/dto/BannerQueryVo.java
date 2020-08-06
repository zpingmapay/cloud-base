package com.xyz.sample.client.dto;

import lombok.Data;

import java.util.Date;

public abstract class BannerQueryVo {

    @Data
    public static class Request extends SearchPage {

        /** 活动标题 */
        private String activityTitle;
        /** 活动状态 0待上线 1已上线 2已下线 */
        private Integer activityStatus;
        /** banner图对应app_id 1汽油应用 2柴油应用 */
        private String appId;
        /** 活动省份 */
        private String activityProvince;
        /** 活动城市 */
        private String activityCity;
        /** 活动是否有效 1 有效 0 无效 */
        private Integer status;
        /** 纬度 暂不支持根据经纬度查询 */
        private Double lat;
        /** 经度 暂不支持根据经纬度查询 */
        private Double lng;

        /** 排序方式 1 上线时间 2 下线时间 默认按上线时间 倒序 */
        private Integer mode = 1;
        /** 排序方向 1、顺序  2、倒序 */
        private Integer direction = 2;
    }

    @Data
    public static class Response extends BannerBaseDto {

        /** id */
        private String id;

        /** 上线时间 */
        private Date startTime;

        /** 下线时间 */
        private Date endTime;

        /** banner图片url */
        private String fileUrl;

        /** 活动url */
        private String activityUrl;

        /** 活动标题 */
        private String activityTitle;

        /** 活动状态 0待上线 1已上线 2已下线 */
        private Integer activityStatus;

        /** 活动状态描述 */
        private String activityStatusDesc;

        /** banner图对应app_id */
        private String appId;
        /** 活动是否有效 1 有效 0 无效 */
        private Integer status;
        /** banner跳转类型 0 无跳转 1 app/小程序页面 2 H5页面 */
        private Integer jumpType;
        /** 站点id */
        private String stationId;

    }

}
