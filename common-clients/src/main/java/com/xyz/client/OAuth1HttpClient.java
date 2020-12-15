package com.xyz.client;

import com.xyz.client.config.ClientCredentialConfig;
import com.xyz.exception.CommonException;
import com.xyz.utils.BeanUtils;
import com.xyz.utils.JsonUtils;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class OAuth1HttpClient {

    private final CloseableHttpClient httpClient;
    private final ClientCredentialConfig clientCredentialConfig;

    public OAuth1HttpClient(CloseableHttpClient httpClient, ClientCredentialConfig clientCredentialConfig) {
        this.clientCredentialConfig = clientCredentialConfig;
        this.httpClient = httpClient;
    }

    public String doGet(String url, Object parameters) {
        return doGet(url, parameters, Collections.emptyMap());
    }

    public String doGet(String url, Object parameters, Map<String, String> headers) {
        return doGet(url, parameters, headers, this::responseToString);
    }

    public <T> T doGet(String url, Object parameters, Map<String, String> headers, Function<CloseableHttpResponse, T> responseHandler) {
        String getUrl = UriComponentsBuilder.fromHttpUrl(url).queryParams(initQueryParams(parameters)).toUriString();
        HttpGet httpGet = new HttpGet(getUrl);
        HttpClientUtils.logRequest(getUrl, parameters, httpGet.getMethod());
        Instant start = Instant.now();

        try {
            T res = execute(httpGet, headers, responseHandler);
            HttpClientUtils.logResponse(getUrl, res, start);
            return res;
        } catch (Exception e) {
            HttpClientUtils.logError(e, start);
            throw new RuntimeException(e);
        }
    }

    public String doPost(String url, Object bodyDto) {
        return this.doPost(url, bodyDto, this::responseToString);
    }

    public <T> T doPost(String url, Object bodyDto, Function<CloseableHttpResponse, T> responseHandler) {
        return this.doPost(url, bodyDto, Collections.emptyMap(), responseHandler);
    }

    public <T> T doPost(String url, Object bodyDto, Map<String, String> headers, Function<CloseableHttpResponse, T> responseHandler) {
        return this.doPost(url, JsonUtils.beanToJson(bodyDto), headers, responseHandler);
    }

    public String doPost(String url, String body) {
        return this.doPost(url, body, Collections.emptyMap());
    }

    public String doPost(String url, String body, Map<String, String> headers) {
        return this.doPost(url, body, headers, this::responseToString);
    }

    public <T> T doPost(String url, String body, Map<String, String> headers, Function<CloseableHttpResponse, T> responseHandler) {
        HttpPost httpPost = new HttpPost(url);
        StringEntity httpEntity = new StringEntity(body, Charsets.UTF_8);
        httpPost.setEntity(httpEntity);
        HttpClientUtils.logRequest(url, body, httpPost.getMethod());
        Instant start = Instant.now();

        try {
            T res = execute(httpPost, headers, responseHandler);
            HttpClientUtils.logResponse(url, res, start);
            return res;
        } catch (Exception e) {
            HttpClientUtils.logError(e, start);
            throw new RuntimeException(e);
        }
    }

    private <T> T execute(HttpUriRequest request, Map<String, String> headers, Function<CloseableHttpResponse, T> responseHandler) throws Exception {
        HttpClientUtils.addTraceableHeaders(request);
        HttpClientUtils.addContentType(request);
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach(request::addHeader);
        }

        String url = request.getURI().toString();
        ClientCredentialConfig.OAuthConfig oauthKey = clientCredentialConfig.findOAuthConfigByUrl(url);
        if (oauthKey != null) {
            Oauth1Signer.getOrCreate(oauthKey.getKey(), oauthKey.getSecret()).sign(request);
        } else {
            log.warn("lack of OAuth key and secret Config, url: {}", url);
        }
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return responseHandler.apply(response);
        }
    }

    public String responseToString(CloseableHttpResponse response) {
        ValidationUtils.notNull(response, "No response");
        ValidationUtils.notNull(response.getEntity(), "No response");
        try {
            return IOUtils.toString(response.getEntity().getContent()).replaceAll("\n", "");
        } catch (IOException e) {
            throw new CommonException(null, "Extract response failed", e);
        }
    }

    private MultiValueMap<String, String> initQueryParams(Object obj) {
        Map<String, Object> map = BeanUtils.beanToMap(obj);
        LinkedMultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        map.forEach((key, value) -> valueMap.add(key, String.valueOf(value)));
        return valueMap;
    }
}
