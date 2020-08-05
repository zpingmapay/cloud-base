package com.xyz.cloud.oauth1;

import com.xyz.cloud.oauth1.annotation.OAuth1Secured;
import com.xyz.cloud.trace.holder.DefaultHeadersHolder;
import com.xyz.cloud.trace.holder.HttpHeadersHolder;
import com.xyz.exception.AccessException;
import com.xyz.exception.ValidationException;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Objects;

import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

@Slf4j
@Aspect
@Order(1000)
public class OAuth1Aspect {
    private static final String HEADER_AUTH_TOKEN = "Authorization";
    private static final String OAUTH_PREFIX = "OAuth ";
    private static final String CONSUMER_KEY = "oauth_consumer_key";

    private final OAuth1Validator oAuth1Validator;

    public OAuth1Aspect(OAuth1Validator oAuth1Validator) {
        this.oAuth1Validator = oAuth1Validator;
    }

    @Around(value = "@annotation(annotation) || @within(annotation)", argNames = "pjp,annotation")
    public Object authWithOAuth1(ProceedingJoinPoint pjp, OAuth1Secured annotation) throws Throwable {
        try {
            validateOAuth1Token();
            return pjp.proceed();
        } catch (ValidationException e) {
            throw new AccessException(e.getCode(), e.getMsg());
        }
    }

    private void validateOAuth1Token() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();

        HttpHeadersHolder httpHeadersHolder = new DefaultHeadersHolder();
        httpHeadersHolder.extract(request);

        String token = httpHeadersHolder.getString(HEADER_AUTH_TOKEN);
        ValidationUtils.isTrue(StringUtils.isNotBlank(token) && token.startsWith(OAUTH_PREFIX), "Invalid oauth token");

        String[] keyPairs = token.substring(OAUTH_PREFIX.length()).split(",");
        String consumerKey = Arrays
                .stream(keyPairs)
                .map(x -> {
                    String[] strArr = x.trim().split("=");
                    return Pair.of(strArr[0], strArr[1].substring(1,
                            strArr[1].length() - 1));
                }).filter(k -> CONSUMER_KEY.equals(k.getKey()))
                .map(Pair::getValue).findAny()
                .orElse(null);

        ValidationUtils.notBlank(consumerKey, "Invalid consumer key");

        oAuth1Validator.validateRequest(consumerKey, request);
    }
}
