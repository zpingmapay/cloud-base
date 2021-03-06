package com.xyz.cloud.oauth1;

import com.google.common.collect.Maps;
import com.xyz.exception.AccessException;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import net.oauth.*;
import net.oauth.server.OAuthServlet;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class OAuth1Validator {
    private static final SimpleOAuthValidator validator = new SimpleOAuthValidator();
    private static final AtomicLong lastReleaseTime = new AtomicLong(System.currentTimeMillis());
    private static final long releasePeriod = 3600000;  // one hour

    private final OAuthServerConfig oAuthServerConfig;

    public OAuth1Validator(OAuthServerConfig oAuthServerConfig) {
        this.oAuthServerConfig = oAuthServerConfig;
    }

    public void validateRequest(String consumerKey, HttpServletRequest httpRequest) {
        ValidationUtils.isTrue(oAuthServerConfig!=null && MapUtils.isNotEmpty(oAuthServerConfig.getOauth()), "Server side oauth keys are missing");
        String consumerSecret = oAuthServerConfig.findConsumerSecretByKey(consumerKey);
        ValidationUtils.notBlank(consumerSecret, "Invalid OAuth1 consumer key");
        String baseUrl = oAuthServerConfig.findBaseUrlByKey(consumerKey);
        String url = convertUrl(baseUrl, httpRequest.getRequestURL().toString(), httpRequest.getRequestURI());
        OAuthMessage message = OAuthServlet.getMessage(httpRequest, url);
        OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumerSecret, null);
        consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);
        OAuthAccessor accessor = new OAuthAccessor(consumer);
        try {
            validator.validateMessage(message, accessor);
        } catch (Exception e) {
            log.error("Invalid OAuth1 token, url: {},consumerKey: {}", url, consumerKey);
            throw new AccessException("Invalid OAuth1 token", e);
        } finally {
            //Workaround to avoid OutOfMemoryError
            if (System.currentTimeMillis() - lastReleaseTime.get() > releasePeriod) {
                CompletableFuture.runAsync(() -> {
                    validator.releaseGarbage();
                    lastReleaseTime.set(System.currentTimeMillis());
                });
            }
        }
    }

    private String convertUrl(String baseUrl, String requestUrl, String requestUri) {
        if (StringUtils.isBlank(baseUrl)) {
            return requestUrl;
        }
        return baseUrl + requestUri;
    }
}
