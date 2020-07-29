package com.xyz.cloud.trace.threadpool;

import com.google.common.collect.Maps;
import com.xyz.function.ThrowingFunction;
import com.xyz.utils.Uuid;
import org.slf4j.MDC;

import java.util.Map;
import java.util.function.Consumer;

public interface ContextAwareable {
    String TID = "tid";
    String UID = "uid";

    Map<String, String> getThreadContextMap();

    default Map<String, String> copyOrInitMdcCtx() {
        Map<String, String> mdcCtx = MDC.getCopyOfContextMap();
        if (mdcCtx == null) {
            mdcCtx = Maps.newConcurrentMap();
        }
        if (!mdcCtx.containsKey(TID)) {
            mdcCtx.put(TID, Uuid.shortUuid());
        }
        return mdcCtx;
    }

    default <T> T execute(ThrowingFunction<Void, T> function) throws Exception {
        Map<String, String> threadContextMap = this.getThreadContextMap();
        if (threadContextMap != null) {
            MDC.setContextMap(threadContextMap);
        }

        try {
            return function.applyThrows(null);
        } finally {
            try {
                MDC.clear();
            } catch (Throwable e) {
                //ignored
            }
        }
    }

    default void consume(Consumer<Void> consumer) {
        Map<String, String> threadContextMap = this.getThreadContextMap();
        if (threadContextMap != null) {
            MDC.setContextMap(threadContextMap);
        }

        try {
            consumer.accept(null);
        } finally {
            try {
                MDC.clear();
            } catch (Throwable e) {
                //ignored
            }
        }
    }
}
