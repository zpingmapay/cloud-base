package com.xyz.cloud.lock.provider;

public interface LockProvider {
    Lock getLock(String key);

    interface Lock {
        boolean tryLock(long waitInMillis, long lockInMills);

        boolean isLocked();

        void unlock();
    }
}
