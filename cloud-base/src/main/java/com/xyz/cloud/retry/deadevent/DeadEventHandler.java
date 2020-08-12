package com.xyz.cloud.retry.deadevent;

import com.xyz.cloud.retry.RetryableEvent;

public interface DeadEventHandler {
    <T extends RetryableEvent> void handleDeadEvent(String listenerClassName, String actionMethodName, T event);
}
