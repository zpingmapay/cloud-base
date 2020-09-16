package com.xyz.desensitize.util;

import com.github.houbb.sensitive.core.api.SensitiveUtil;
import com.xyz.utils.JsonUtils;

import java.util.Collection;
import java.util.List;

/**
 * 脱敏工具类
 *
 * @author sxl
 * @since 2020/9/14 15:59
 */
public final class DesensitizeUtil {

    private DesensitizeUtil() {
    }

    /**
     * 脱敏对象
     *
     * @param object 原始对象
     * @param <T>    泛型
     * @return 脱敏后的对象
     * @since 0.0.4 以前用的是单例。建议使用 spring 等容器管理 ISensitive 实现。
     */
    public static <T> T desensitizeObj(T object) {
        String json = SensitiveUtil.desJson(object);
        @SuppressWarnings("unchecked")
        T desObj = (T) JsonUtils.jsonToBean(json, object.getClass());
        return desObj;
    }

    /**
     * 返回脱敏后的对象 json
     * null 对象，返回字符串 "null"
     *
     * @param object 对象
     * @return 结果 json
     * @since 0.0.6
     */
    public static String desensitizeJson(Object object) {
        return SensitiveUtil.desJson(object);
    }

    /**
     * 脱敏对象集合
     *
     * @param collection 原始集合
     * @param <T>        泛型
     * @return 脱敏后的对象集合，如果原始对象为 {@link com.github.houbb.heaven.util.util.CollectionUtil#isEmpty(Collection)}，则返回空列表。
     */
    public static <T> List<T> desensitizeCollection(Collection<T> collection) {
        return SensitiveUtil.desCopyCollection(collection);
    }

    /**
     * 脱敏对象 JSON 集合
     *
     * @param collection 原始集合
     * @return 脱敏后的对象集合，如果原始对象为 {@link com.github.houbb.heaven.util.util.CollectionUtil#isEmpty(Collection)}，则返回空列表。
     */
    public static List<String> desensitizeJsonCollection(Collection<?> collection) {
        return SensitiveUtil.desJsonCollection(collection);
    }
}
