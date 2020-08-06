package com.xyz.client.feign.interceptor;

import feign.Logger;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * outbound logger
 *
 * @author sxl
 */
@Slf4j
public class OutboundLogger extends Logger {

    @Override
    protected void log(String configKey, String format, Object... args) {
        log.info("{}:{}", configKey, String.format(format, args));
    }

//    @Override
//    protected void logRequest(String configKey, Logger.Level logLevel, Request request) {
//        String url = request.url();
//        String params = request.body() == null ? StringUtils.EMPTY : new String(request.body(), request.charset());
//        Request.HttpMethod httpMethod = request.httpMethod();
//        log.info("请求第三方路径开始: url: {}, 参数: {}, 请求方式: {}, configKey: {}",
//                url, params, httpMethod, configKey);
//    }

//    @Override
//    protected Response logAndRebufferResponse(String configKey, Logger.Level logLevel, Response response, long elapsedTime)
//            throws IOException {
//        if (response.body() != null) {
//            String result = "";
//            byte[] bodyData = Util.toByteArray(response.body().asInputStream());
//            if (bodyData.length > 0) {
//                result = Util.decodeOrDefault(bodyData, Util.UTF_8, "Binary data");
//            }
//            Response buildResponse = response.toBuilder().body(bodyData).build();
//            Request request = buildResponse.request();
//            log.info("请求第三方路径完成: url: {}, 响应结果: {}, configKey: {},耗时: {}ms",
//                    request.url(), result, configKey, elapsedTime);
//            return buildResponse;
//        }
//
//        return response;
//    }


}