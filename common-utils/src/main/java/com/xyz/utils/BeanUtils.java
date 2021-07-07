package com.xyz.utils;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BeanUtils {
    public static <T> T mapToBean(Map<String, Object> map, Class<T> beanClass) {
        try {
            T t = beanClass.newInstance();

            if (MapUtils.isEmpty(map)) {
                return t;
            }

            BeanInfo beanInfo = Introspector.getBeanInfo(t.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                Method setter = property.getWriteMethod();
                if (setter != null) {
                    /*log.debug("cur property name :{}", property.getName());*/
                    setter.invoke(t, map.get(property.getName()));
                }
            }
            return t;
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public static Map<String, Object> beanToMap(Object beanObj) {

        if (null == beanObj) {
            return Collections.emptyMap();
        }

        Map<String, Object> map = new HashMap<>();

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanObj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (key.compareToIgnoreCase("class") == 0) {
                    continue;
                }
                Method getter = property.getReadMethod();
                Object value = getter != null ? getter.invoke(beanObj) : null;
                if (value == null) {
                    continue;
                }
                map.put(key, value);
            }
            return map;
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public static boolean isAllFieldsSet(Object obj) {
        Map<String, Object> map = beanToMap(obj);
        return map.values().stream().allMatch(BeanUtils::isFieldSet);
    }

    public static boolean isAnyFieldSet(Object obj) {
        Map<String, Object> map = beanToMap(obj);
        return map.values().stream().anyMatch(BeanUtils::isFieldSet);
    }

    private static boolean isFieldSet(Object fieldValue) {
        if (fieldValue == null) {
            return false;
        } else if (fieldValue instanceof Collection) {
            return ((Collection<?>) fieldValue).size() > 0;
        } else if (fieldValue.getClass().isArray()) {
            return Array.getLength(fieldValue) > 0;
        } else {
            return StringUtils.isNotBlank(fieldValue.toString());
        }
    }
}
