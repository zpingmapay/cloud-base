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
    private static final String REQUEST_LOG_PATTEN = "请求第三方开始: url: {}, 参数: {}, 请求方式: {}, method: {}";
    private static final String RESPONSE_LOG_PATTEN = "请求第三方完成: url: {}, 响应: {}, method: {}, 耗时: {}ms";
    private static final String ERROR_LOG_PATTEN = "请求第三方失败: 错误: {}, method: {}, 耗时: {}ms";

    @Override
    protected void log(String s, String s1, Object... objects) {
    }

    @Override
    protected void logRequest(String configKey, Logger.Level logLevel, Request request) {
        String url = request.url();
        String params = request.body() == null ? StringUtils.EMPTY : new String(request.body(), request.charset());
        Request.HttpMethod httpMethod = request.httpMethod();
        log.info(REQUEST_LOG_PATTEN, url, params, httpMethod, configKey);
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Logger.Level logLevel, Response response, long elapsedTime)
            throws IOException {
        if (response.body() != null) {
            String result = "";
            byte[] bodyData = Util.toByteArray(response.body().asInputStream());
            if (bodyData.length > 0) {
                result = Util.decodeOrDefault(bodyData, Util.UTF_8, "Binary data");
            }
            Response buildResponse = response.toBuilder().body(bodyData).build();
            Request request = buildResponse.request();
            log.info(RESPONSE_LOG_PATTEN, request.url(), result, configKey, elapsedTime);
            return buildResponse;
        }

        return response;
    }

    @Override
    protected IOException logIOException(String configKey, Level logLevel, IOException ioe, long elapsedTime) {
        log.warn(ERROR_LOG_PATTEN, ioe.getMessage(), configKey, elapsedTime, ioe);
        return ioe;
    }
}