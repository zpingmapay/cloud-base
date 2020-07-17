package com.xyz.cloud.retry;

import com.xyz.cloud.retry.annotation.Retryable;
import com.xyz.cloud.retry.sotre.EventStore;
import com.xyz.utils.JsonUtils;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Slf4j
@Aspect
public class RetryableAspect {
    private final EventStoreFactory eventStoreFactory;
    private final EventStore eventStoreTemplate;

    public RetryableAspect(EventStoreFactory eventStoreFactory, EventStore eventStoreTemplate) {
        this.eventStoreFactory = eventStoreFactory;
        this.eventStoreTemplate = eventStoreTemplate;
    }

    @Around("@annotation(com.xyz.cloud.retry.annotation.Retryable)")
    public Object onEventHandling(ProceedingJoinPoint pjp) throws Throwable {
        Retryable retryableInfo = getRetryAbleInfo(pjp);
        ValidationUtils.notNull(retryableInfo, "Retryable event is not properly configured");
        RetryableEvent event = getRetryAbleEvent(pjp);
        ValidationUtils.notNull(event, "Not a Retryable Event arg");

        EventStore eventStore = eventStoreFactory.findOrCreate(event.getClass(), eventStoreTemplate.getClass());
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        String listenerClassName = methodSignature.getDeclaringTypeName();
        String actionMethodName = methodSignature.getName();
        try {
            Object result = pjp.proceed();
            eventStore.remove(listenerClassName, actionMethodName, event);
            return result;
        } catch (RetryableException e) {
            //First attempt
            if (event.getAttempts() == 0) {
                log.warn("Failed to handle event {}, will retry later", JsonUtils.beanToJson(event));
                event.attemptIncrementAndGet();
                eventStore.add(listenerClassName, actionMethodName, event, retryableInfo.maxAttempts());
                return null;
            }
            //Exceed max attempts
            if (event.getAttempts() > retryableInfo.maxAttempts()) {
                eventStore.remove(listenerClassName, actionMethodName, event);
                //TODO manually process the failed event is needed here
                log.error("Failed to handle event {} after {} attempts", JsonUtils.beanToJson(event), retryableInfo.maxAttempts(), e);
                return null;
            }
            //Normal attempt
            log.warn("Failed to handle event {}, at attempts {}", JsonUtils.beanToJson(event), event.getAttempts());
            event.attemptIncrementAndGet();
            eventStore.update(listenerClassName, actionMethodName, event);
            return null;
        }
    }

    private Retryable getRetryAbleInfo(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        return method.getAnnotation(Retryable.class);
    }

    private RetryableEvent getRetryAbleEvent(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        ValidationUtils.isTrue(args != null && args.length >= 1, "No args found");
        ValidationUtils.isTrue(args[0] instanceof RetryableEvent, "Arg is not RetryableEvent");
        return (RetryableEvent) args[0];
    }
}
