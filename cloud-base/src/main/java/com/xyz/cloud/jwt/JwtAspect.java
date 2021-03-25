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
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

import static com.xyz.cloud.jwt.JwtTokenProvider.HEADER_ACCESS_TOKEN;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

@Slf4j
@Aspect
@Order(1000)
public class JwtAspect {
    private final JwtTokenProvider jwtTokenProvider;
    private final ApplicationContext ctx;

    public JwtAspect(JwtTokenProvider jwtTokenProvider, ApplicationContext ctx) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.ctx = ctx;
    }

    @Around(value = "@annotation(annotation) || @within(annotation)", argNames = "pjp,annotation")
    public Object authWithJwt(ProceedingJoinPoint pjp, JwtSecured annotation) throws Throwable {
        try {
            annotation = getAnnotation(pjp);
            validJwt(annotation);
        } catch (ValidationException e) {
            throw new AccessException(e.getMessage());
        }
        return pjp.proceed();
    }

    private void validJwt(JwtSecured annotation) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(requestAttributes).getRequest();

        String token = request.getHeader(HEADER_ACCESS_TOKEN);
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(HEADER_ACCESS_TOKEN);

        }
        ValidationUtils.isTrue(token != null, "Access token is required");

        JwtTokenFactory jwtTokenFactory = getTokenFactory(annotation);
        if(jwtTokenFactory != null) {
            token = jwtTokenFactory.findByKey(token);
        }
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        ValidationUtils.isTrue(StringUtils.isNotBlank(userId), "Invalid access token");

        RequestContextHolder.getRequestAttributes().setAttribute(JwtTokenProvider.USER_ID, userId, RequestAttributes.SCOPE_REQUEST);
    }

    private JwtSecured getAnnotation(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();

        return method.getAnnotation(JwtSecured.class);
    }

    private JwtTokenFactory getTokenFactory(JwtSecured annotation) {
        if(annotation == null) {
            return null;
        }
        String tokenFactoryBeanName = annotation.tokenFactory();
        if(StringUtils.isBlank(tokenFactoryBeanName)) {
            return null;
        }
        return ctx.getBean(tokenFactoryBeanName, JwtTokenFactory.class);
    }

}
