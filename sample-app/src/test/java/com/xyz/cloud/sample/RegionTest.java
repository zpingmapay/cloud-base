package com.xyz.cloud.sample;

import com.xyz.geo.Region;
import com.xyz.geo.RegionManager;
import com.xyz.utils.JsonUtils;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author sxl
 * @since 2021/4/1 11:11
 */
@SpringBootTest
public class RegionTest {

    @Test
    public void testRegion() {
        List<Region> regions = RegionManager
                .regionsByProvinceCodes(Lists.newArrayList("410000","610000"));
        System.out.println("method: testLock1, param: regions= " + JsonUtils.beanToJson(regions));

        regions = RegionManager
                .regionsByCityCodes(Lists.newArrayList("611000","610000"));
        System.out.println("method: testLock1, param: regions= " + JsonUtils.beanToJson(regions));
    }
}
