package com.xyz.geo;

import lombok.Data;

/**
 * @author sxl
 */
@Data
public class Region {
    /** 城市code */
    private String cityCode;
    /** 城市名 */
    private String cityName;
    /** 上级城市级id 省的parentId为1 */
    private String parentId;
    /** 城市类型,0=省,1=市,2=区/县 */
    private Integer regionType;
    /**
     * 城市中心经纬度,lng,lat
     */
    private String center;
    /**
     * 城市中心纬度lat
     */
    private Double lat;
    /**
     * 城市中心经度,lng
     */
    private Double lng;

    public void setCenter(String center) {
        this.center = center;
        String[] split = center.split(",");
        this.lat = Double.valueOf(split[1]);
        this.lng = Double.valueOf(split[0]);
    }

    public enum RegionType {
        /**
         * 区域类型
         */
        PROVINCE(0, "省"),
        CITY(1, "市"),
        DISTRICT(2, "区/县"),
        ;

        /**
         * 类型
         */
        public Integer type;
        /**
         * 类型描述
         */
        public String desc;

        RegionType(Integer type, String desc) {
            this.type = type;
            this.desc = desc;
        }
    }

}
