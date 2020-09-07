package com.xyz.client.feign.interceptor;

import com.xyz.client.config.ClientCredentialConfig;
import com.xyz.exception.ValidationException;
import com.xyz.utils.JsonUtils;
import com.xyz.utils.TimeUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.*;

/**
 * Sapi Request signer
 *
 * @author sxl
 */
@Slf4j
public class SapiRequestSigner implements RequestInterceptor {
    private static final String HEADER_REQUEST_METHOD = "method";
    private final ClientCredentialConfig credentialConfig;

    public SapiRequestSigner(ClientCredentialConfig clientCredentialConfig) {
        this.credentialConfig = clientCredentialConfig;
    }

    @Override
    public void apply(RequestTemplate template) {
        // 通过method区分不同接口
        String method = extractMethod(template);
        // 业务参数
        String data = new String(template.body(), template.requestCharset());
        data = JsonUtils.beanToJson(JsonUtils.jsonToBean(data, TreeMap.class));
        // 对应验签配置
        ClientCredentialConfig.SignerConfig signerConfig = credentialConfig.findSignerConfigByUrl(template.feignTarget().url());
        // 时间戳参数
        String timestamp = TimeUtils.format(new Date(), TimeUtils.DATE_FULL_STR);
        String appId = signerConfig.getAppId();
        // 签名
        String sign = sign(appId, signerConfig.getAppKey(), method, timestamp, data);

        // 最终参数
        SapiParam sapiParam = new SapiParam();
        sapiParam.setMethod(method);
        sapiParam.setData(data);
        sapiParam.setAppId(appId);
        sapiParam.setTimestamp(timestamp);
        sapiParam.setSign(sign);

        // 重新设置自定义后body
        template.body(JsonUtils.beanToJson(sapiParam));
    }

    @Data
    private static class SapiParam {
        private String method;
        private String data;
        private String appId;
        private String timestamp;
        private String sign;
    }

    private String extractMethod(RequestTemplate template) {
        Map<String, Collection<String>> headers = template.headers();
        Optional<String> methodOp = headers.get(HEADER_REQUEST_METHOD).stream().findFirst();
        if (!methodOp.isPresent()) {
            log.error("请求SAPI接口 >>> [header缺少method参数],headers:{}", JsonUtils.beanToJson(headers));
            throw new ValidationException("请求SAPI接口,header缺少method参数");
        }
        return methodOp.get();
    }

    /**
     * @return 签名
     */
    private static String sign(String appId, String appKey, String method, String timestamp, String param) {
        String md5 = appKey.concat(appId).concat(param).concat(method).concat(timestamp).concat(appKey);
        return DigestUtils.md5Hex(md5).toUpperCase();
    }

}