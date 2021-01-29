package com.xyz.cloud.jwt;

import com.xyz.cloud.jwt.annotation.JwtSecured;
import com.xyz.exception.AccessException;
import com.xyz.exception.ValidationException;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.xyz.cloud.jwt.JwtTokenProvider.HEADER_ACCESS_TOKEN;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

@Slf4j
@Aspect
@Order(1000)
public class JwtAspect {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAspect(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Around(value = "@annotation(annotation) || @within(annotation)", argNames = "pjp,annotation")
    public Object authWithJwt(ProceedingJoinPoint pjp, JwtSecured annotation) throws Throwable {
        try {
            validJwt();
        } catch (ValidationException e) {
            throw new AccessException(e.getMessage());
        }
        return pjp.proceed();
    }

    private void validJwt() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();

        String token = request.getHeader(HEADER_ACCESS_TOKEN);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(HEADER_ACCESS_TOKEN);
        }
        ValidationUtils.isTrue(token != null, "Access token is required");
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        ValidationUtils.isTrue(StringUtils.isNotBlank(userId), "Invalid access token");

        RequestContextHolder.getRequestAttributes().setAttribute(JwtTokenProvider.USER_ID, userId, RequestAttributes.SCOPE_REQUEST);
    }

}
