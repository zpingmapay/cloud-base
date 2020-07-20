package com.xyz.cloud.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xyz.cache.ICache;
import com.xyz.utils.ValidationUtils;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.auth0.jwt.JWT.create;

public class JwtTokenProvider {
    public static final String HEADER_ACCESS_TOKEN = "authorization";
    public static final String WEB_TOKEN_PREFIX = "Bearer ";
    public static final String APP_ID = "x-app-id";
    public static final String USER_ID = "x-user-id";
    static final String CACHE_NAMESPACE = JwtTokenProvider.class.getName();
    private final JwtConfig jwtConfig;
    private final ICache<String, String> cache;

    public JwtTokenProvider(JwtConfig jwtConfig, ICache<String, String> cache) {
        this.jwtConfig = jwtConfig;
        this.cache = cache;
    }

    public String buildJwtToken(String userId) {
        try {
            String jwtToken = WEB_TOKEN_PREFIX + create()
                    .withIssuer(jwtConfig.getAppId())
                    .withClaim(USER_ID, userId)
                    .withClaim(APP_ID, jwtConfig.getAppId())
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getTtlInHours() * 60 * 60 * 1000L))
                    .sign(Algorithm.HMAC256(jwtConfig.getSecret()));
            cacheToken(userId, jwtToken.substring(WEB_TOKEN_PREFIX.length()));
            return jwtToken;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Build token failed");
        }
    }

    public String getUserIdFromToken(String jwtToken) {
        jwtToken = jwtToken.substring(WEB_TOKEN_PREFIX.length());
        DecodedJWT jwt = getDecodedJWT(jwtToken);
        if (Objects.isNull(jwt)) {
            return null;
        }
        String userId = jwt.getClaim(USER_ID).asString();
        if (jwtConfig.isMultiLoginCheck()) {
            multiLoginCheck(userId, jwtToken);
        }
         return userId;
    }

    private DecodedJWT getDecodedJWT(String jwtToken) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtConfig.getSecret()))
                    .withIssuer(jwtConfig.getAppId()).build();
            return verifier.verify(jwtToken);
        } catch (Exception e) {
            return null;
        }
    }

    private void cacheToken(String userId, String token) {
        this.cache.put(userId, token, jwtConfig.getTtlInHours(), TimeUnit.HOURS);
    }

    void multiLoginCheck(String userId, String token) {
        String cachedToken = this.cache.get(userId);
        ValidationUtils.notBlank(cachedToken, "Access token is expired");
        ValidationUtils.isTrue(cachedToken.equals(token), "Account logged in another device, please login again");
    }
}
