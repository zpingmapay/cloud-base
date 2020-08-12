package com.xyz.cloud.oauth1;

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

    private final OAuthServerConfig oAuthServerConfig;

    public OAuth1Validator(OAuthServerConfig oAuthServerConfig) {
        this.oAuthServerConfig = oAuthServerConfig;
    }

    public void validateRequest(String consumerKey, HttpServletRequest httpRequest) {
        String consumerSecret = oAuthServerConfig.findConsumerSecretByKey(consumerKey);
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
            //Workaround to avoid OutOfMemoryError
            if (System.currentTimeMillis() - lastReleaseTime.get() > releasePeriod) {
                CompletableFuture.runAsync(() -> {
                    validator.releaseGarbage();
                    lastReleaseTime.set(System.currentTimeMillis());
                });
            }
        }
    }
}
