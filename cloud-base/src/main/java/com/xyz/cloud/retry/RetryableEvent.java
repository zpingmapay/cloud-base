package com.xyz.cloud.retry;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Retryable event for eventually consistency.
 * These events MUST be handled in an idempotent way.
 */
@Data
@NoArgsConstructor
public class RetryableEvent {
    private Object data;
    private long timestamp = System.currentTimeMillis();
    private String traceId; //uuid
    private int attempts;

    public RetryableEvent(@NotNull Object data, @NotBlank String traceId) {
        this.data = data;
        this.traceId = traceId;
    }

    public int attemptIncrementAndGet() {
        synchronized (traceId) {
            return ++attempts;
        }
    }
}
