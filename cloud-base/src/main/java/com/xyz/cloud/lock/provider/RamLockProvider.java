package com.xyz.cloud.lock.provider;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class RamLockProvider implements LockProvider {
    private final Cache<String, RamLock> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(60, TimeUnit.MINUTES)
            .build();


    @Override
    public Lock getLock(String key) {
        RamLock lock = cache.getIfPresent(key);
        if(lock == null) {
            lock = new RamLock(new ReentrantLock());
            cache.put(key, lock);
        }
        return lock;
    }

    public static class RamLock implements Lock {
        private final java.util.concurrent.locks.Lock lock;
        private final AtomicLong expireAt = new AtomicLong(0);
        private final AtomicBoolean locked = new AtomicBoolean(false);

        public RamLock(java.util.concurrent.locks.Lock lock) {
            this.lock = lock;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expireAt.get();
        }

        @Override
        public synchronized boolean tryLock(long ttlInMills) {
            if(this.isLocked()) {
                return false;
            }
            locked.compareAndSet(false, true);
            expireAt.compareAndSet(0, System.currentTimeMillis() + ttlInMills);
            return this.lock.tryLock();
        }


        @Override
        public boolean isLocked() {
            return locked.get() && !isExpired();
        }

        @Override
        public void unlock() {
            locked.set(false);
            expireAt.set(0);
            this.lock.unlock();
        }

    }
}
