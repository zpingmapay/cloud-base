package com.xyz.sample.client.dto;

import lombok.Data;

import java.util.List;

/**
 * @author 7580
 */
@Data
public class BannerBaseDto {

    /** 活动省份 */
    private List<String> activityProvinces;
    /** 活动城市 */
    private List<String> activityCities;
}
