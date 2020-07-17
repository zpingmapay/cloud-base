package com.xyz.cloud.lock.provider;

public interface LockProvider {
    String CACHE_NAMESPACE = LockProvider.class.getName();

    Lock getLock(String key);

    interface Lock {
        boolean tryLock(long ttlInMills);

        boolean isLocked();

        void unlock();
    }
}
