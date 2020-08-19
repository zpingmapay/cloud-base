package com.xyz.cloud.lock.provider;

public interface LockProvider {
    Lock getLock(String key);

    interface Lock {
        boolean tryLock(long waitInMillis, long ttlInMills);

        boolean isLocked();

        void unlock();
    }
}
