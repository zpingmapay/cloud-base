package com.xyz.cloud.trace.threadpool;

import com.google.common.collect.Maps;
import com.xyz.utils.Uuid;
import org.slf4j.MDC;

import java.util.Map;

public interface ContextAwareable {
    String TID = "tid";
    String UID = "uid";

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
}
