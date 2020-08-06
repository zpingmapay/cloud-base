package com.xyz.client;

import com.xyz.cache.CacheManager;
import com.xyz.exception.CommonException;
import com.xyz.utils.BeanUtils;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;
import org.apache.commons.codec.Charsets;
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
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class OAuth1HttpClient {
    public static final String HEADER_AUTH_TOKEN = "Authorization";

    private final CloseableHttpClient httpClient;
    private final String consumerKey;
    private final String consumerSecret;

    private OAuth1HttpClient(CloseableHttpClient httpClient, String consumerKey, String consumerSecret) {
        this.httpClient = httpClient;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public static OAuth1HttpClient getOrCreate(CloseableHttpClient httpClient, String consumerKey, String consumerSecret) {
        return CacheManager.getFromLocalOrCreate(OAuth1HttpClient.class.getName(), consumerKey, (x) -> new OAuth1HttpClient(httpClient,
                consumerKey, consumerSecret));
    }

    public <T> T doGet(String url, Object parameters, Function<CloseableHttpResponse, T> responseHandler) throws Exception {
        String getUrl = UriComponentsBuilder.fromHttpUrl(url).queryParams(initQueryParams(parameters)).toUriString();
        HttpGet httpGet = new HttpGet(getUrl);
        HttpClientUtils.logRequest(getUrl, parameters, httpGet.getMethod());
        Instant start = Instant.now();

        try {
            T res = execute(httpGet, responseHandler);
            HttpClientUtils.logResponse(getUrl, res, start);
            return res;
        } catch (Exception e) {
            HttpClientUtils.logError(e, start);
            throw e;
        }
    }

    public String doGet(String url, Object parameters) throws Exception {
        return doGet(url, parameters, this::responseToString);
    }

    public <T> T doPost(String url, String body, Function<CloseableHttpResponse, T> responseHandler) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        StringEntity httpEntity = new StringEntity(body, Charsets.UTF_8);
        httpPost.setEntity(httpEntity);
        HttpClientUtils.logRequest(url, body, httpPost.getMethod());
        Instant start = Instant.now();

        try {
            T res = execute(httpPost, responseHandler);
            HttpClientUtils.logResponse(url, res, start);
            return res;
        } catch (Exception e) {
            HttpClientUtils.logError(e, start);
            throw e;
        }
    }

    public String doPost(String url, String body) throws Exception {
        return this.doPost(url, body, this::responseToString);
    }


    public <T> T execute(HttpUriRequest request, Function<CloseableHttpResponse, T> responseHandler) throws Exception {
        HttpClientUtils.addTraceableHeaders(request);
        HttpClientUtils.addContentType(request);
        sign(request);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            return responseHandler.apply(response);
        }
    }

    public String sign(HttpUriRequest request) throws OAuthCommunicationException, OAuthExpectationFailedException,
            OAuthMessageSignerException {
        OAuthConsumer oauthConsumer = CacheManager.getFromLocalOrCreate(CommonsHttpOAuthConsumer.class.getName()
                , consumerKey, (x) -> {
                    OAuthConsumer consumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
                    consumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
                    return consumer;
                });

        HttpRequest signedRequest = oauthConsumer.sign(request);
        return signedRequest.getHeader(HEADER_AUTH_TOKEN);
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
