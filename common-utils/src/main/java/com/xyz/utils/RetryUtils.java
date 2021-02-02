package com.xyz.utils;

import com.xyz.exception.RetryException;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Predicate;

/**
 * @author sxl
 * @since 2021/2/2 16:52
 */
@Slf4j
public abstract class RetryUtils {

    /**
     * 不断尝试执行,直到predicate返回true,或者业务逻辑抛出异常
     *
     * @param businessPredicate 业务逻辑Predicate,返回true时代表执行业务执行完毕,不会重试执行,返回false会进行重试执行
     * @param data              业务逻辑数据
     * @param <T>               业务逻辑数据类型
     */
    public static <T> void doWithRetry(Predicate<T> businessPredicate, T data) {
        if (businessPredicate.test(data)) {
            return;
        }
        while (true) {
            if (businessPredicate.test(data)) {
                return;
            }
        }
    }

    /**
     * 不断尝试执行,直到predicate返回true,或者达到最大重试次数,,或者业务逻辑抛出异常
     *
     * @param businessPredicate 业务逻辑Predicate,返回true时代表执行业务执行完毕,不会重试执行,返回false会进行重试执行
     * @param data              业务逻辑数据
     * @param retryTimes        重试次数,总最大尝试次数=retryTimes+1
     * @param <T>               业务逻辑数据类型
     */
    public static <T> void doWithRetry(Predicate<T> businessPredicate, T data, int retryTimes) {
        if (businessPredicate.test(data)) {
            return;
        }
        for (int i = 0; i < retryTimes; i++) {
            if (businessPredicate.test(data)) {
                return;
            }
        }
        log.error("执行失败,重试次数达到上限,data: {}", JsonUtils.beanToJson(data));
        throw new RetryException("执行失败,重试次数达到上限");
    }

}
