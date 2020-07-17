package com.xyz.cloud.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Objects;

import static com.auth0.jwt.JWT.create;

public class JwtTokenProvider {
    public static final String HEADER_ACCESS_TOKEN = "Authorization";
    public static final String WEB_TOKEN_PREFIX = "Bearer ";
    public static final String APP_ID = "x-app-id";
    public static final String USER_ID = "x-user-id";
    static final String CACHE_PREFIX = "cloud.jwt.";
    private final JwtConfig jwtConfig;
    private final RMapCache<String, String> rMapCache;

    public JwtTokenProvider(JwtConfig jwtConfig, RedissonClient redissonClient) {
        this.jwtConfig = jwtConfig;
        this.rMapCache = redissonClient.getMapCache(CACHE_PREFIX.concat(jwtConfig.getAppId()), StringCodec.INSTANCE);
    }

    public String buildJwtToken(String userId) {
        try {
            String jwtToken = WEB_TOKEN_PREFIX + create()
                    .withIssuer(jwtConfig.getAppId())
                    .withClaim(USER_ID, userId)
                    .withClaim(APP_ID, jwtConfig.getAppId())
                    .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getTtlInHours() * 60 * 60 * 1000L))
                    .sign(Algorithm.HMAC256(jwtConfig.getSecret()));
            rMapCache.put(userId, jwtToken);
            return jwtToken;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Build token failed");
        }
    }

    public String getInfoByToken(String jwtToken) {
        jwtToken = jwtToken.substring(7);
        DecodedJWT jwt = getDecodedJWT(jwtToken);
        if (Objects.isNull(jwt)) {
            return null;
        }
        return jwt.getClaim(USER_ID).asString();
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
}
