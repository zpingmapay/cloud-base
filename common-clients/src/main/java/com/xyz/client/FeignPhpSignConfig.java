package com.xyz.client;

import com.google.common.base.Joiner;
import com.xyz.exception.ValidationException;
import com.xyz.utils.JsonUtils;
import com.xyz.utils.TimeUtils;
import feign.Request;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Feign client php接口签名配置
 *
 * @author sxl
 */
@Slf4j
public class FeignPhpSignConfig implements RequestInterceptor {
    public static final String HEADER_APP_ID_PREFIX = "app-id=";
    public static final String HEADER_APP_KEY_PREFIX = "app-key=";

    private static final int FORM_PARAM_SIZE = 5;
    private static final String HEADER_REQUEST_METHOD = "method";
    private static final String HEADER_APP_ID = "app-id";
    private static final String HEADER_APP_KEY = "app-key";

    /**
     * php接口参数,签名处理
     */
    @Override
    public void apply(RequestTemplate template) {
        // php不同接口是通过method区分的
        String method = extractMethod(template);
        // 业务参数
        Map<String, Object> data = bodyParams(template);
        // 时间戳参数
        String timestamp = String.valueOf(TimeUtils.currentSecondTimestamp());
        // 签名
        String appId = extractAppId(template);
        String sign = sign(appId, extractAppKey(template), method, timestamp, data);
        // 最终参数
        Map<String, String> formParams = new HashMap<>(FORM_PARAM_SIZE);
        formParams.put("method", method);
        formParams.put("data", JsonUtils.beanToJson(data));
        formParams.put("appId", appId);
        formParams.put("timestamp", timestamp);
        formParams.put("sign", sign);

        // 重新设置自定义后body
        template.body(Request.Body.encoded(buildBodyStr(formParams).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8));
    }

    private String extractAppId(RequestTemplate template) {
        return this.extractHeaderValue(template, HEADER_APP_ID);
    }

    private String extractAppKey(RequestTemplate template) {
        return this.extractHeaderValue(template, HEADER_APP_KEY);
    }

    private String extractHeaderValue(RequestTemplate template, String headerName) {
        Map<String, Collection<String>> headers = template.headers();
        if (MapUtils.isNotEmpty(headers)) {
            Collection<String> headerValues = headers.get(headerName);
            if (CollectionUtils.isNotEmpty(headerValues)) {
                Optional<String> first = headerValues.stream().findFirst();
                if (first.isPresent()) {
                    // 移除铭感信息
                    template.header(headerName, Collections.emptyList());
                    return first.get();
                }
            }
        }
        log.error("请求头中缺少app-id信息,headers : {}", JsonUtils.beanToJson(headers));
        throw new IllegalStateException("请求头中缺少app-id信息");
    }

    private String extractMethod(RequestTemplate template) {
        Map<String, Collection<String>> headers = template.headers();
        Optional<String> methodOp = headers.get(HEADER_REQUEST_METHOD).stream().findFirst();
        if (!methodOp.isPresent()) {
            log.error("请求PHP接口 >>> [header缺少method参数],headers:{}", JsonUtils.beanToJson(headers));
            throw new ValidationException("请求PHP接口,header缺少method参数");
        }
        return methodOp.get();
    }

    private Map<String, Object> bodyParams(RequestTemplate template) {
        Map<String, Object> paramMap = new TreeMap<>();
        Request.Body body = template.requestBody();
        if (body.length()==0) {
            return paramMap;
        }
        //relId=2824&setModel=21&setType=2&setVal=-20&skuCode=D100G06&stationId=3254
        String bodyStr = body.asString();
        for (String param : StringUtils.split(bodyStr, "&")) {
            String[] kv = StringUtils.split(param, "=");
            paramMap.put(kv[0], kv[1]);
        }
        return paramMap;
    }

    /**
     * 签名排列列顺序固定为： data=xxx&mess=xxx&timestamp=xxx&key=appkey
     *
     * @param paramMap 参数map
     * @return form表单body参数
     */
    private String buildBodyStr(Map<String, String> paramMap) {
        return Joiner.on("&").withKeyValueSeparator("=").join(paramMap);
    }

    /**
     * @return 签名
     */
    private static String sign(String appId, String appKey, String method, String timestamp, Map<String, Object> paramMap) {
        String param = Joiner.on(",").withKeyValueSeparator("=").join(paramMap);
        String md5 = appKey.concat(appId).concat(param).concat(method).concat(timestamp).concat(appKey);
        return DigestUtils.md5Hex(md5).toUpperCase();
    }

}