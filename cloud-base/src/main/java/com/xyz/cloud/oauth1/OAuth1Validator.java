package com.xyz.cloud.oauth1;

import com.xyz.cloud.oauth1.provider.OAuth1KeyProvider;
import com.xyz.exception.AccessException;
import com.xyz.utils.ValidationUtils;
import net.oauth.*;
import net.oauth.server.OAuthServlet;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;

public class OAuth1Validator {
    private static final SimpleOAuthValidator validator = new SimpleOAuthValidator();
    private static final AtomicLong lastReleaseTime = new AtomicLong(System.currentTimeMillis());
    private static final long releasePeriod = 3600000;  // one hour

    private final OAuth1KeyProvider oAuth1KeyProvider;

    public OAuth1Validator(OAuth1KeyProvider oAuth1KeyProvider) {
        this.oAuth1KeyProvider = oAuth1KeyProvider;
    }

    public void validateRequest(String consumerKey, HttpServletRequest httpRequest) {
        String consumerSecret = oAuth1KeyProvider.findConsumerSecretByKey(consumerKey);
        ValidationUtils.notBlank(consumerSecret, "Invalid OAuth1 consumer key");
        OAuthMessage message = OAuthServlet.getMessage(httpRequest, null);
        OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumerSecret, null);
        consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);
        OAuthAccessor accessor = new OAuthAccessor(consumer);
        try {
            validator.validateMessage(message, accessor);
        } catch (Exception e) {
            throw new AccessException("Invalid OAuth1 token");
        } finally {
            if (System.currentTimeMillis() - lastReleaseTime.get() > releasePeriod) {
                CompletableFuture.runAsync(() -> {
                    validator.releaseGarbage();
                    lastReleaseTime.set(System.currentTimeMillis());
                });
            }
        }
    }
}
