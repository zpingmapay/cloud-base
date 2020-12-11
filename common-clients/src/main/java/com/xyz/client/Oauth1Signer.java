package com.xyz.client;

import com.xyz.cache.CacheManager;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * @author sxl
 * @since 2020/12/11 11:18
 */
public class Oauth1Signer {
    public static final String HEADER_AUTH_TOKEN = "Authorization";

    private final String consumerKey;
    private final String consumerSecret;

    private Oauth1Signer(String consumerKey, String consumerSecret) {
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public static Oauth1Signer getOrCreate(String consumerKey, String consumerSecret) {
        return CacheManager.getFromLocalOrCreate(Oauth1Signer.class.getName(), consumerKey,
                (x) -> new Oauth1Signer(consumerKey, consumerSecret));
    }

    public String sign(HttpUriRequest request) throws OAuthCommunicationException, OAuthExpectationFailedException,
            OAuthMessageSignerException {
        OAuthConsumer oauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        oauthConsumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
        HttpRequest signedRequest = oauthConsumer.sign(request);
        return signedRequest.getHeader(HEADER_AUTH_TOKEN);
    }

}
