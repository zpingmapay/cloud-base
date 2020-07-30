package com.xyz.cloud.lock.provider;

import com.xyz.cache.CacheManager;
import com.xyz.cache.ICache;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class LocalLockProvider implements LockProvider {
    private final ICache<String, LocalLock> cache = CacheManager.getLocalCache(CACHE_NAMESPACE);

    @Override
    public synchronized Lock getLock(String key) {
        LocalLock lock = cache.get(key);
        if(lock == null) {
            lock = new LocalLock(new ReentrantLock());
            cache.putIfAbsent(key, lock);
        }
        return lock;
    }

    public static class LocalLock implements Lock {
        private final java.util.concurrent.locks.Lock lock;
        private final AtomicLong expireAt = new AtomicLong(0);
        private final AtomicBoolean locked = new AtomicBoolean(false);

        public LocalLock(java.util.concurrent.locks.Lock lock) {
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
