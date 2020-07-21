package com.xyz.cloud.retry;

import com.xyz.cloud.retry.annotation.Retryable;
import com.xyz.cloud.retry.deadevent.DeadEventHandler;
import com.xyz.cloud.retry.repository.EventRepository;
import com.xyz.cloud.retry.repository.EventRepositoryFactory;
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
    private final EventRepositoryFactory eventRepositoryFactory;
    private final EventRepository eventRepositoryTemplate;
    private final DeadEventHandler deadEventHandler;

    public RetryableAspect(EventRepositoryFactory eventRepositoryFactory, EventRepository eventRepositoryTemplate, DeadEventHandler deadEventHandler) {
        this.eventRepositoryFactory = eventRepositoryFactory;
        this.eventRepositoryTemplate = eventRepositoryTemplate;
        this.deadEventHandler = deadEventHandler;
    }

    @Around("@annotation(com.xyz.cloud.retry.annotation.Retryable)")
    public Object onEventHandling(ProceedingJoinPoint pjp) throws Throwable {
        Retryable retryableInfo = getRetryAbleInfo(pjp);
        ValidationUtils.notNull(retryableInfo, "Retryable event is not properly configured");
        RetryableEvent event = getRetryAbleEvent(pjp);
        ValidationUtils.notNull(event, "Not a Retryable Event arg");

        EventRepository eventRepository = eventRepositoryFactory.findOrCreate(event.getClass(), eventRepositoryTemplate.getClass());
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        String listenerClassName = methodSignature.getDeclaringTypeName();
        String actionMethodName = methodSignature.getName();
        EventRepository.EventItem<? extends RetryableEvent> item = EventRepository.EventItem.create(listenerClassName, actionMethodName, event, retryableInfo.maxAttempts());

        try {
            Object result = pjp.proceed();
            eventRepository.remove(item);
            return result;
        } catch (RetryableException e) {
            //First attempt
            if (event.getAttempts() == 0) {
                log.warn("Failed to handle event {}, will retry later", JsonUtils.beanToJson(event));
                event.attemptIncrementAndGet();
                eventRepository.add(item);
                return null;
            }
            log.warn("Failed to handle event {}, at attempts {}", JsonUtils.beanToJson(event), event.getAttempts());
            event.attemptIncrementAndGet();
            //Exceed max attempts
            if (event.getAttempts() > retryableInfo.maxAttempts()) {
                eventRepository.remove(item);
                deadEventHandler.handleDeadEvent(listenerClassName, actionMethodName, event);
            } else {
                //Normal attempt
                eventRepository.update(item);
            }
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
