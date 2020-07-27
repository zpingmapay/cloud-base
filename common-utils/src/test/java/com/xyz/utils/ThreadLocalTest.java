package com.xyz.utils;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ThreadLocalTest {
    public static class MyContext {
        private static final ThreadLocal<String> threadLocal = ThreadLocal.withInitial(Uuid::generate);

        public void setMyId(String userId) {
            threadLocal.set(userId);
        }

        public String getMyId() {
            return threadLocal.get();
        }

        public void print() {
            System.out.println(String.format("t:%s, uid:%s", Thread.currentThread().getName(), getMyId()));
        }

        public void remove() {
            threadLocal.remove();
        }
    }

    @Test
    public void testMultiInstance() {
        MyContext ctx = new MyContext();
        String userId = ctx.getMyId();

        ctx = new MyContext();
        Assert.isTrue(ctx.getMyId().equals(userId), "not the same user-id");
    }

    @Test
    public void testMultiThread() {
        int size = 10;
        Set<String> userIds = IntStream.range(0, size).mapToObj(
                x -> {
                    try {
                        return CompletableFuture.supplyAsync(() -> {
                            MyContext ctx = new MyContext();
                            //               ctx.setMyId(Uuid.generate());
                            String userId = ctx.getMyId();
                            ctx.print();
                            ctx.remove();
                            return userId;
                        }).get();
                    } catch (Exception e) {
                        return null;
                    }
                }
        ).collect(Collectors.toSet());

        Assert.isTrue(!userIds.contains(null), "null found");
        Assert.isTrue(userIds.size() == size, "not all returned");
    }
}
