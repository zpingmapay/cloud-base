package com.xyz.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.net.URL;

/**
 * 资源加载工具
 * 本应用为jar包应用，主要使用classpath路径
 * @see this getClassPathFile
 */
@Slf4j
public class ResourceUtil {

    private static final String PATH_WINDOWS = "\\\\";

    private static final String PATH_LINUX = "/";

    /**
     * 读取jar包文件
     * @param pathName 路径
     * @return inputStream
     * @throws FileNotFoundException
     */
    public static URL getJarURL(String pathName) throws FileNotFoundException {
        try {
            ClassPathResource classPathResource = new ClassPathResource(pathName);
            return classPathResource.getURL();
        } catch (IOException e) {
            log.error("ResourceUtil.getResourcePath 文件不存在, path:{}", pathName);
            throw new FileNotFoundException(pathName + "文件不存在");
        }
    }

    /**
     * jar包内文件的获取
     * @param classPath 因为性对路径问题，通过classpath获取比较好
     * @return 文件 classpath:config/overview.properties
     * @throws FileNotFoundException ex
     */
    @Deprecated
    public static File getClassPathFile(String classPath) throws FileNotFoundException {
        try {
            return ResourceUtils.getFile(classPath);
        } catch (FileNotFoundException e) {
            log.error("ResourceUtil.getResourcePath 文件不存在, path:{}", classPath);
            throw new FileNotFoundException(classPath + "文件不存在");
        }
    }

    /**
     * 获取文件路径
     * @param path 文件路径
     * @return URL
     */
    public static URL getResourcePath(String path) throws FileNotFoundException {
        try {
            //1.以Linux路径为准
            path = path.replaceAll(PATH_WINDOWS, PATH_LINUX);

            /*
              2.依据开头自主选择加载方法
              第一：前面有 "/" 代表了工程的根目录,例如工程名叫做myproject,"/"代表了myproject
              第二：前面没有 "" 代表当前类的目录
             */
            return path.startsWith(PATH_LINUX) ?
                    ResourceUtil.class.getResource(path) :
                    ResourceUtil.class.getClassLoader().getResource(path);
        } catch (Exception e) {
            log.error("ResourceUtil.getResourcePath 文件不存在, path:{}", path);
            throw new FileNotFoundException(path + "文件不存在");
        }
    }

    /**
     * 获取文件
     * @see #getJarURL(String path)
     */
    public static File getJarFile(String path) throws FileNotFoundException {
        try {
            ClassPathResource classPathResource = new ClassPathResource(path);
            InputStream is = classPathResource.getInputStream();
            File tempFile = File.createTempFile("groovy", null);
            IOUtils.copy(is, new FileOutputStream(tempFile));
            return tempFile;
        } catch (IOException e) {
            log.error("ResourceUtil.getResourcePath 文件不存在, path:{}", path);
            throw new FileNotFoundException(path + "文件不存在");
        }
    }
}