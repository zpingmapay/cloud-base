package com.xyz.cloud.sample.client;

import com.xyz.client.OAuth1HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class OAuth1HttpClientTest {
    @Resource
    private CloseableHttpClient httpClient;

    @Test
    @Ignore
    public void testDoGet() throws Exception {
        String url = "http://localhost:1008/me";
        String consumerKey = "oauth1_consumer_key_of_sample_service";
        String consumerSecret = "oauth1_consumer_secret_of_sample_service";

        OAuth1HttpClient oAuth1HttpClient = OAuth1HttpClient.getOrCreate(httpClient, consumerKey, consumerSecret);
        String userId = oAuth1HttpClient.doGet(url, null);
        System.out.println(userId);
    }
}
