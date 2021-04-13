package com.xyz.cloud.lock;

import com.xyz.cloud.lock.annotation.Lock;
import com.xyz.cloud.lock.provider.LockProvider;
import com.xyz.cloud.spel.SpelUtils;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import javax.validation.constraints.NotNull;

@Slf4j
@Aspect
public class LockAspect {
    private final LockProvider lockProvider;

    public LockAspect(@NotNull LockProvider lockProvider) {
        this.lockProvider = lockProvider;
    }

    @Around(value = "@annotation(annotation)", argNames = "pjp,annotation")
    public Object lock(ProceedingJoinPoint pjp, Lock annotation) throws Throwable {
        ValidationUtils.notNull(annotation, "Lock is not properly configured");
        ValidationUtils.notBlank(annotation.key(), "Lock is not properly configured");
        String lockKey = SpelUtils.parse(annotation.key(), pjp);

        LockProvider.Lock lock = lockProvider.getLock(lockKey);
        boolean locked = false;
        try {
            assertTrue(lock!=null, "Lock is not obtained");
            locked = lock.tryLock(annotation.waitInMillis(), annotation.lockInMillis());
            assertTrue(locked, "Lock is not obtained");

            return pjp.proceed();
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    private void assertTrue(boolean condition, String msg) {
        if(!condition) {
            throw new FailedToObtainLockException(msg);
        }
    }
}
