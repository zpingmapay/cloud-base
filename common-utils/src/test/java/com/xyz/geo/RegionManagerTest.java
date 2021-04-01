package com.xyz.geo;

import com.xyz.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author sxl
 * @since 2021/4/1 10:47
 */
class RegionManagerTest {

    @Test
    void cityByDistrict() {
    }

    @Test
    void firstCityByName() {
    }

    @Test
    void districtByName() {
    }

    @Test
    void cityNameByDistrict() {
    }

    @Test
    void getNameByCode() {
    }

    @Test
    void provinceByName() throws IOException {
        //URL resourcePath = ResourceUtil.getResourcePath("classpath*:region.json");
        //
        //String string = FileUtils.readFileToString(jarFile);
        //System.out.println("string = " + string);

        Region region = RegionManager.provinceByName("河南省");
        System.out.println("method: provinceByName, param: region= " + JsonUtils.beanToJson(region));
    }

    @Test
    void provinceByCityName() {
    }

    @Test
    void provinceByCode() {
    }

    @Test
    void regionByProvinceCode() {
    }

    @Test
    void regionByCityCode() {
    }

    @Test
    void regionByDistrictCode() {
    }

    @Test
    void testProvinceByName() {
    }

    @Test
    void provinceByRegion() {
    }

    @Test
    void findByCode() {
    }

    @Test
    void allCity() {
    }

    @Test
    void isSpecialProvince() {
    }

    @Test
    void isSpecialCity() {
    }

    @Test
    void filterRegions() {
    }

    @Test
    void testFilterRegions() {
    }
}