package com.xyz.sample.client;

import com.xyz.client.OAuth1HttpClient;
import com.xyz.cloud.dto.ResultDto;
import com.xyz.utils.JsonUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;

@SpringBootTest
@Disabled
public class OAuth1HttpClientTest {
    @Resource
    private CloseableHttpClient httpClient;
    @Resource
    private OAuth1HttpClient oAuth1HttpClient;

    @Test
    public void testDoGet() throws Exception {
        String url = "http://localhost:1008/me?userName=test";
        String consumerKey = "oauth1_consumer_key_of_sample_service";
        String consumerSecret = "oauth1_consumer_secret_of_sample_service";
        //OAuth1HttpClient oAuth1HttpClient = OAuth1HttpClient.getOrCreate(httpClient, consumerKey, consumerSecret);
        ResultDto<String> result = oAuth1HttpClient.doGet(url, null, null, (x) -> {
            String response = oAuth1HttpClient.responseToString(x);
            return JsonUtils.jsonToBean(response, ResultDto.class, String.class);
        });
        Assert.isTrue(result.resultOk(), "Failed to get my user id");
    }
}
