package com.xyz.cloud.sample.client;

import com.xyz.client.HttpClientUtils;
import com.xyz.client.OAuth1HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Ignore
public class OAuth1HttpClientTest {
    @Test
    public void testDoGet() throws Exception {
        String url = "http://localhost:1008/me";
        String consumerKey = "oauth1_consumer_key_of_sample_service";
        String consumerSecret = "oauth1_consumer_secret_of_sample_service";

        try (CloseableHttpClient httpClient = HttpClientUtils.buildHttpClient(6000, 30000, 200, 20)) {
            OAuth1HttpClient oAuth1HttpClient = OAuth1HttpClient.getOrCreate(httpClient, consumerKey, consumerSecret);
            String userId = oAuth1HttpClient.doGet(url, null);
            System.out.println(userId);
        }
    }
}
