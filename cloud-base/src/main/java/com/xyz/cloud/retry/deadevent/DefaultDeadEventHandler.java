package com.xyz.cloud.retry.deadevent;

import com.xyz.cloud.retry.RetryableEvent;
import com.xyz.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultDeadEventHandler implements DeadEventHandler {
    @Override
    public <T extends RetryableEvent> void handleDeadEvent(String listenerClassName, String actionMethodName, T event) {
        log.error("Failed to handle event {}.{}, {} after max attempts", listenerClassName, actionMethodName, JsonUtils.beanToJson(event));
    }
}
