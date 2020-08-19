package com.xyz.cloud.lock.provider;

import com.xyz.cache.CacheManager;
import com.xyz.cache.ICache;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class LocalLockProvider implements LockProvider {
    private final ICache<String, LocalLock> cache = CacheManager.getLocalCache(LockProvider.class.getName());

    @Override
    public Lock getLock(String key) {
        return cache.getOrCreate(key, (x) -> new LocalLock(new ReentrantLock()));
    }

    public static class LocalLock implements Lock {
        private final ReentrantLock reentrantLock;

        public LocalLock(ReentrantLock reentrantLock) {
            this.reentrantLock = reentrantLock;
        }

        @Override
        public boolean tryLock(long waitInMills, long ttlInMills) {
            try {
                return this.reentrantLock.tryLock(waitInMills, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                return false;
            }
        }

        @Override
        public boolean isLocked() {
            return reentrantLock.isLocked();
        }

        @Override
        public void unlock() {
            this.reentrantLock.unlock();
        }
    }
}
