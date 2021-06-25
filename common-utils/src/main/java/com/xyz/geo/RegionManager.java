package com.xyz.geo;

import com.google.common.collect.Sets;
import com.xyz.exception.ValidationException;
import com.xyz.geo.Region.RegionType;
import com.xyz.utils.JsonConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author sxl
 * @since 2020/6/1 10:24
 */
@Slf4j
public class RegionManager {

    private static final String REGION_CONFIG_NAME = "geo/region.json";

    public static List<Region> regions = readRegion();

    /**
     * key: 省名称, 市+区名称
     * value: Region
     */
    private static final Map<String, Region> regionNameMap;
    /**
     * key: cityCode
     * value: Region
     */
    private static final Map<String, Region> regionCodeMap;

    private static final Set<String> SPECIAL_PROVINCE_NAMES = Sets.newHashSet("天津市", "上海市", "北京市", "重庆市");
    private static final Set<String> SPECIAL_CITY_NAMES = Sets.newHashSet("天津市区", "上海市区", "北京市区", "重庆市区", "重庆郊县");

    private static List<Region> readRegion() {
        return JsonConfig.config2List(REGION_CONFIG_NAME, Region.class);
    }

    static {
        Map<String, Region> codeMap = regions.stream().collect(Collectors.toMap(Region::getCityCode, Function.identity()));
        regionCodeMap = Collections.unmodifiableMap(codeMap);

        // 省
        Map<String, Region> nameMap = regions.stream().filter(RegionManager::isProvince)
                                             .collect(Collectors.toMap(Region::getCityName, Function.identity()));

        // 市
        Map<String, Region> cityMap = regions.stream().filter(RegionManager::isCity)
                                             .collect(Collectors.toMap(Region::getCityName, Function.identity(), (k1, k2) -> k1));

        // 区
        Map<String, Region> districtMap = regions.stream().filter(RegionManager::isDistrict)
                                                 .collect(Collectors.toList())
                                                 .stream()
                                                 .collect(Collectors.toMap(e ->
                                                                 regionCodeMap.get(e.getParentId()).getCityName() + e.getCityName(),
                                                         Function.identity()));
        nameMap.putAll(districtMap);
        nameMap.putAll(cityMap);

        regionNameMap = Collections.unmodifiableMap(nameMap);

    }

    public static Optional<Region> cityByDistrict(String cityName, String districtName) {
        Optional<Region> districtOp = districtByName(cityName, districtName);
        if (districtOp.isPresent()) {
            return regionByCityCode(districtOp.get().getParentId());
        }
        return firstCityByName(cityName);
    }

    public static Optional<Region> firstCityByName(String cityName) {
        Region city = regionNameMap.get(legalCityName(cityName));
        if (city == null) {
            return Optional.empty();
        }
        return Optional.of(city);
    }

    public static Optional<Region> districtByName(String cityName, String districtName) {
        String legalCityName = Objects.requireNonNull(legalCityName(cityName));
        String cityDistrict = legalCityName + Objects.requireNonNull(districtName);
        Region district = regionNameMap.get(cityDistrict);
        if (!isDistrict(district)) {
            return Optional.empty();
        }
        return Optional.of(district);
    }

    public static String cityNameByDistrict(String cityName, String districtName) {
        Optional<Region> cityOp = cityByDistrict(cityName, districtName);
        return cityOp.isPresent() ? cityOp.get().getCityName() : "";
    }

    public static String getNameByCode(String code) {
        Region region = regionCodeMap.get(Objects.requireNonNull(code));
        return region == null ? "" : region.getCityName();
    }

    public static Region provinceByName(String provinceName) {
        return regionNameMap.get(legalProvinceName(provinceName));
    }

    public static Region provinceByCityName(String cityName) {
        String legalCityName = legalCityName(cityName);
        Region city = regions.stream()
                             .filter(region -> region.getCityName().equals(legalCityName))
                             .findFirst()
                             .orElseThrow(() -> new ValidationException("城市名称错误" + cityName));
        return regionCodeMap.get(city.getParentId());
    }

    public static Optional<Region> provinceByCode(String code) {
        Optional<Region> provinceOp = regionByProvinceCode(code);
        if (provinceOp.isPresent()) {
            return provinceOp;
        }

        provinceOp = provinceByCityCode(code);
        if (provinceOp.isPresent()) {
            return provinceOp;
        }

        Optional<Region> districtOp = regionByDistrictCode(code);
        if (!districtOp.isPresent()) {
            return districtOp;
        }
        return provinceByCityCode(districtOp.get().getParentId());
    }

    public static Optional<Region> regionByProvinceCode(String code) {
        Region province = regionCodeMap.get(code);
        if (!isProvince(province)) {
            return Optional.empty();
        }
        return Optional.of(province);
    }

    public static List<Region> regionsByProvinceCodes(List<String> provinceCodes) {
        return regionCodes(provinceCodes, RegionManager::regionByProvinceCode);
    }

    public static List<Region> regionsByCityCodes(List<String> cityCodes) {
        return regionCodes(cityCodes, RegionManager::regionByCityCode);
    }

    public static List<Region> regionsByDistrictCodes(List<String> districtCodes) {
        return regionCodes(districtCodes, RegionManager::regionByDistrictCode);
    }

    public static List<Region> regionCodes(List<String> codes, Function<String, Optional<Region>> getRegion) {
        if (CollectionUtils.isEmpty(codes)) {
            return Collections.emptyList();
        }

        return codes.stream()
                    .map(code -> getRegion.apply(code).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
    }

    public static Optional<Region> regionByCityCode(String code) {
        Region city = regionCodeMap.get(code);
        if (!isCity(city)) {
            return Optional.empty();
        }
        return Optional.of(city);
    }

    public static Optional<Region> regionByDistrictCode(String code) {
        Region district = regionCodeMap.get(code);
        if (!isDistrict(district)) {
            return Optional.empty();
        }
        return Optional.of(district);
    }

    private static Optional<Region> provinceByCityCode(String code) {
        Optional<Region> cityOp = regionByCityCode(code);
        return cityOp.map(region -> regionCodeMap.get(region.getParentId()));
    }

    private static boolean isCity(Region region) {
        return isRegionType(region, RegionType.CITY);
    }

    private static boolean isProvince(Region region) {
        return isRegionType(region, RegionType.PROVINCE);
    }

    private static boolean isDistrict(Region region) {
        return isRegionType(region, RegionType.DISTRICT);
    }

    private static boolean isRegionType(Region region, RegionType regionType) {
        if (region == null) {
            return false;
        }
        return regionType.type.equals(region.getRegionType());
    }


    public static Region provinceByName(String provinceName, boolean isLike) {

        String legalProvinceName = legalProvinceName(provinceName);
        if (isLike) {
            List<Region> provinces = regions.stream().filter(region -> region.getCityName().startsWith(legalProvinceName)
                    && RegionType.PROVINCE.type.equals(region.getRegionType())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(provinces)) {
                return null;
            }
            if (provinces.size() == 1) {
                return provinces.get(0);
            }
            log.error("当前关键字包含多个省份,无法确定哪一个!");
            throw new RuntimeException();
        }
        return provinceByName(legalProvinceName);
    }

    public static Region provinceByRegion(Region region) {
        if (region == null) {
            return null;
        }

        // 直接就是省
        if (RegionType.PROVINCE.type.equals(region.getRegionType())) {
            return region;
        }
        // 市
        if (RegionType.CITY.type.equals(region.getRegionType())) {
            return findByCode(region.getParentId()).orElseThrow(() -> new ValidationException("region error"));
        }
        // 区/县
        String parentId = findByCode(region.getParentId())
                .orElseThrow(() -> new ValidationException("region error")).getParentId();
        return findByCode(parentId).orElseThrow(() -> new ValidationException("region error"));
    }

    public static Optional<Region> findByCode(String regionCode) {
        return regions.stream()
                      .filter(region -> region.getCityCode().equals(regionCode))
                      .findFirst();
    }

    public static List<Region> allCity() {
        return RegionManager.regions.stream()
                                    .filter(region -> RegionType.CITY.type.equals(region.getRegionType()))
                                    .collect(Collectors.toList());
    }

    private static String legalProvinceName(String provinceName) {
        if (isSpecialProvince(provinceName)) {
            provinceName = StringUtils.removeEnd(provinceName, "市");
        }
        return provinceName;
    }

    public static boolean isSpecialProvince(String provinceName) {
        return SPECIAL_PROVINCE_NAMES.contains(provinceName);
    }

    private static String legalCityName(String cityName) {
        if (isSpecialCity(cityName)) {
            if ("重庆郊县".equals(cityName)) {
                cityName = "重庆市";
            } else {
                cityName = StringUtils.removeEnd(cityName, "区");
            }
        }
        return cityName;
    }

    public static boolean isSpecialCity(String cityName) {
        return SPECIAL_CITY_NAMES.contains(cityName);
    }

    public static List<Region> filterRegions(Predicate<Region> predicate) {
        return filterRegions(regions, predicate);
    }

    public static List<Region> filterRegions(List<Region> regions, Predicate<Region> predicate) {
        if (CollectionUtils.isEmpty(regions)) {
            return Collections.emptyList();
        }
        return regions.stream()
                      .filter(predicate)
                      .collect(Collectors.toList());
    }

    public static <R> List<R> regionConvert(List<Region> regions, Function<Region, R> regionConverter) {
        if (CollectionUtils.isEmpty(regions)) {
            return Collections.emptyList();
        }
        return regions.stream()
                      .map(regionConverter)
                      .collect(toList());
    }

}
