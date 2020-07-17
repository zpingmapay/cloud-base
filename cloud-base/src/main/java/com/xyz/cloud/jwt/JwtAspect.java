package com.xyz.cloud.jwt;

import com.xyz.exception.AccessException;
import com.xyz.exception.ValidationException;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.xyz.cloud.jwt.JwtTokenProvider.CACHE_PREFIX;
import static com.xyz.cloud.jwt.JwtTokenProvider.HEADER_ACCESS_TOKEN;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

@Slf4j
@Aspect
@Order(1000)
public class JwtAspect {
    private final JwtTokenProvider jwtTokenProvider;
    private final RMapCache<String, String> rMapCache;

    public JwtAspect(JwtConfig jwtConfig, JwtTokenProvider jwtTokenProvider, RedissonClient redissonClient) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.rMapCache = redissonClient.getMapCache(CACHE_PREFIX.concat(jwtConfig.getAppId()), StringCodec.INSTANCE);
    }

    @Around("@annotation(com.xyz.cloud.jwt.annotation.Jwt) || @within(com.xyz.cloud.jwt.annotation.Jwt)")
    public Object authWithJwt(ProceedingJoinPoint pjp) throws Throwable {
        try {
            validJwt();
            return pjp.proceed();
        } catch (ValidationException e) {
            throw new AccessException(e.getCode(), e.getMsg(), e);
        }
    }

    private void validJwt() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();
        String authorization = request.getHeader(HEADER_ACCESS_TOKEN);
        if (StringUtils.isBlank(authorization)) {
            authorization = request.getParameter(HEADER_ACCESS_TOKEN);
        }
        ValidationUtils.notBlank(authorization, "Access token is required");
        String userId = jwtTokenProvider.getInfoByToken(authorization);
        ValidationUtils.notBlank(userId, "Invalid access token");

        RequestContextHolder.getRequestAttributes().setAttribute(JwtTokenProvider.USER_ID, userId, RequestAttributes.SCOPE_REQUEST);
        String token = rMapCache.get(userId);
        ValidationUtils.notBlank(token, "Access token is expired");
        ValidationUtils.isTrue(token.equals(authorization), "Account login in another device");
    }
}
