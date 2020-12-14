package com.xyz.utils;


import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class JsonConfig {
    private static final String DEFAULT_CONFIG_DIR = "/etc/xyz/config/";

    public static <T> List<T> config2List(String configName, Class<T> clazz) {
        return config2List(DEFAULT_CONFIG_DIR, configName, clazz);
    }

    public static <T> List<T> config2List(String configFolder, String configName, Class<T> clazz) {
        try {
            String content = readContent(configFolder, configName);
            return JsonUtils.jsonToList(content, clazz);
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    private static String readContent(String configFolder, String configName) {
        String content = null;
        try (InputStream is = new FileInputStream(configFolder.concat(configName))) {
            content = IOUtils.toString(is);
        } catch (Exception e) {
            try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(configName)) {
                content = IOUtils.toString(is);
            } catch (Exception e1) {
                log.warn("no config file found for " + configName);
                return "";
            }
        }
        return content.replaceAll("\r\n", "");
    }
}
