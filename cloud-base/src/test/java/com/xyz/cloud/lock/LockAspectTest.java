package com.xyz.cloud.lock;

import com.xyz.cloud.lock.annotation.Lock;
import com.xyz.cloud.lock.provider.LocalLockProvider;
import com.xyz.cloud.lock.provider.LockProvider;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.util.Assert;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class LockAspectTest {
    private static final String name = "cloud-base";

    @Test
    public void testLockNoWait() {
        LockProvider lockProvider = new LocalLockProvider();
        LockAspectTest proxy = proxy(lockProvider);

        CompletableFuture<String> future = testLock(name, lockProvider, (x) -> proxy.hello(x));
        try {
            future.get();
            fail();
        } catch (Exception e) {
            Assert.isTrue(e.getCause().getClass().equals(FailedToObtainLockException.class), "no lock is expected to be obtained here.");
        }
    }

    @Test
    public void testLockWithWait() throws ExecutionException, InterruptedException {
        LockProvider lockProvider = new LocalLockProvider();
        LockAspectTest proxy = proxy(lockProvider);

        CompletableFuture<String> future = testLock(name, lockProvider, (x) -> proxy.helloAndWait(x));
        Assert.isTrue(name.equals(future.get()), "failed to get lock");
    }

    private LockAspectTest proxy(LockProvider lockProvider) {
        LockAspectTest target = new LockAspectTest();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);

        LockAspect aspect = new LockAspect(lockProvider);
        factory.addAspect(aspect);
        LockAspectTest proxy = factory.getProxy();
        return proxy;
    }

    private CompletableFuture<String> testLock(String name, LockProvider lockProvider, Function<String, String> func) {
        Assert.isTrue(name.equals(func.apply(name)), "hello is not properly executed");

        LockProvider.Lock lock = lockProvider.getLock(name);
        boolean locked = lock.tryLock(0, 20000L);
        ValidationUtils.isTrue(locked, "lock not obtained");

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            LockSupport.parkNanos(20);
            try {
                return func.apply(name);
            } catch (Exception e) {
                log.error("hello failed", e);
                throw e;
            }
        });
        LockSupport.parkNanos(300);
        lock.unlock();
        Assert.isTrue(name.equals(func.apply(name)), "hello is not properly executed");

        return future;
    }

    @Lock(key = "#name")
    public String hello(final String name) {
        log.info("hello {}", name);
        return name;
    }

    @Lock(waitInMillis = 1000, key = "#name")
    public String helloAndWait(final String name) {
        log.info("hello {}", name);
        return name;
    }
}
