package com.xyz.cloud.lock.provider;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

public class RedisLockProvider implements LockProvider {
    private final RedissonClient redissonClient;

    public RedisLockProvider(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Lock getLock(String key) {
        RLock lock = redissonClient.getLock(LockProvider.class.getName().concat(key));
        return new RedisLock(lock);
    }

    public static class RedisLock implements Lock {
        private final RLock lock;

        RedisLock(RLock lock) {
            this.lock = lock;
        }

        @Override
        public boolean tryLock(long waitInMillis, long ttlInMills) {
            try {
                return this.lock.tryLock(waitInMillis, ttlInMills, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public boolean isLocked() {
            return this.lock.isLocked();
        }

        @Override
        public void unlock() {
            this.lock.unlock();
        }
    }
}
