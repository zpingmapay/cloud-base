package com.xyz.cloud.utils;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

public class AtomicLong {
    private final RAtomicLong rAtomicLong;
    private final long cleanValue;
    private static final String namespace = "com.xyz.cloud.atomic.long.";

    public AtomicLong(String key, long initValue, long cleanValue, RedissonClient redissonClient, int ttlInDays) {
        this.rAtomicLong = redissonClient.getAtomicLong(namespace.concat(key));
        if(!rAtomicLong.isExists()) {
            rAtomicLong.set(initValue);
            rAtomicLong.expire(ttlInDays, TimeUnit.DAYS);
        }
        this.cleanValue = cleanValue;
    }

    public final long get() {
        return rAtomicLong.get();
    }

    public final void set(long newValue) {
        rAtomicLong.set(newValue);
        cleanIfNecessary(newValue);
    }

    public final long getAndSet(long newValue) {
        long oldValue = rAtomicLong.getAndSet(newValue);
        cleanIfNecessary(newValue);
        return oldValue;
    }

    public final boolean compareAndSet(long expect, long update) {
        boolean updated = rAtomicLong.compareAndSet(expect, update);
        cleanIfNecessary(update);
        return updated;
    }

    public final long getAndIncrement() {
        long previous = rAtomicLong.getAndDecrement();
        cleanIfNecessary(get());
        return previous;
    }

    public final long getAndDecrement() {
        long previous = rAtomicLong.getAndDecrement();
        cleanIfNecessary(get());
        return previous;
    }

    public final long getAndAdd(long delta) {
        long previous = rAtomicLong.getAndAdd(delta);
        cleanIfNecessary(get());
        return previous;
    }

    public final long incrementAndGet() {
        long updated = rAtomicLong.incrementAndGet();
        cleanIfNecessary(updated);
        return updated;
    }

    public final long decrementAndGet() {
        long updated = rAtomicLong.decrementAndGet();
        cleanIfNecessary(updated);
        return updated;
    }

    public final long addAndGet(long delta) {
        long updated = rAtomicLong.addAndGet(delta);
        cleanIfNecessary(updated);
        return updated;
    }

    public int intValue() {
        return (int) get();
    }

    public boolean isCleaned() {
        return !rAtomicLong.isExists() || get() == cleanValue;
    }

    public long longValue() {
        return get();
    }

    public float floatValue() {
        return (float) get();
    }

    public double doubleValue() {
        return (double) get();
    }

    private void cleanIfNecessary(long update) {
        if (update == cleanValue) {
            rAtomicLong.deleteAsync();
        }
    }
}
