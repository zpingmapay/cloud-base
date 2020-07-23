package com.xyz.cloud.lock;

import com.xyz.cloud.lock.annotation.Lock;
import com.xyz.cloud.lock.provider.LockProvider;
import com.xyz.exception.ValidationException;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;

@Slf4j
@Aspect
public class LockAspect {
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private static final ExpressionParser parser = new SpelExpressionParser();
    private final LockProvider lockProvider;

    public LockAspect(@NotNull LockProvider lockProvider) {
        this.lockProvider = lockProvider;
    }

    @Around(value = "@annotation(annotation)", argNames = "pjp,annotation")
    public Object lock(ProceedingJoinPoint pjp, Lock annotation) throws Throwable {
        ValidationUtils.notNull(annotation, "Lock is not properly configured");
        ValidationUtils.notBlank(annotation.key(), "Lock is not properly configured");
        String lockKey = parseKey(annotation.key(), pjp);

        LockProvider.Lock lock = lockProvider.getLock(lockKey);
        try {
            ValidationUtils.notNull(lock, "Lock is not obtained");
            ValidationUtils.isTrue(lock.tryLock(annotation.lockInMillis()), "Lock is not obtained");

            return pjp.proceed();
        } catch (ValidationException e) {
            throw new FailedToObtainLockException(e.getCode(), e.getMsg(), e);
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    private String parseKey(String key, ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(pjp.getTarget(), method, pjp.getArgs(), parameterNameDiscoverer);
        return parser.parseExpression(key).getValue(context, String.class);
    }
}
