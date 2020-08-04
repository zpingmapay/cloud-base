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
import java.util.Map;
import java.util.function.Function;

import static com.xyz.oauth1.OAuth1KeyProvider.HEADER_AUTH_TOKEN;

@Slf4j
public class OAuth1HttpClient {
    private final CloseableHttpClient httpClient;
    private final String consumerKey;
    private final String consumerSecret;

    public OAuth1HttpClient(CloseableHttpClient httpClient, String consumerKey, String consumerSecret) {
        this.httpClient = httpClient;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public static OAuth1HttpClient getOrCreate(CloseableHttpClient httpClient, String consumerKey, String consumerSecret) {
        return CacheManager.getFromLocalOrCreate(OAuth1HttpClient.class.getName(), consumerKey, (x) -> new OAuth1HttpClient(httpClient, consumerKey, consumerSecret));
    }

    public String doGet(String url, Object parameters) throws Exception {
        String getUrl = UriComponentsBuilder.fromHttpUrl(url).queryParams(initQueryParams(parameters)).toUriString();
        HttpGet httpGet = new HttpGet(getUrl);

        return execute(httpGet, this::responseToString);
    }

    public String doPost(String url, String body) throws Exception {
        HttpPost httpPost = new HttpPost(url);

        StringEntity httpEntity = new StringEntity(body, Charsets.UTF_8);
        httpPost.setEntity(httpEntity);
        return execute(httpPost, this::responseToString);
    }


    public <T> T execute(HttpUriRequest requestBase, Function<CloseableHttpResponse, T> responseHandler) throws Exception {
        HttpClientUtils.addTraceableHeaders(requestBase);

        sign(requestBase);
        try (CloseableHttpResponse response = httpClient.execute(requestBase)) {
            return responseHandler.apply(response);
        }
    }

    public String sign(HttpUriRequest requestBase) throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException {
        OAuthConsumer oauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        oauthConsumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
        HttpRequest signedRequest = oauthConsumer.sign(requestBase);
        return signedRequest.getHeader(HEADER_AUTH_TOKEN);
    }

    private String responseToString(CloseableHttpResponse response) {
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
