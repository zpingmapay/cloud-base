package com.xyz.cloud.sample.lock;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class LockTest {
    @Resource
    private SampleService service;

    @Test
    public void testLock1() {
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            service.execute("1");
        }).thenRunAsync(() -> {
            service.execute("1");
        });
        task1.join();
    }

    @Test
    public void testLock2() {
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            service.execute("1");
        });
        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
            service.execute("1");
        });
        assertException((x) -> CompletableFuture.allOf(task1, task2).join(), CompletionException.class);
    }

    @Test
    public void testLock3() {
        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            service.execute("1");
        });
        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
            service.execute("2");
        });
        CompletableFuture.allOf(task1, task2).join();
    }

    public static <T> void assertException(Consumer<Void> consumer, Class<T> clazz) {
        try {
            consumer.accept(null);
            fail("Expected exception to be thrown here");
        } catch (Exception e) {
            Assert.isTrue(e.getClass().equals(clazz), "Exception expected here!");
        }
    }
}