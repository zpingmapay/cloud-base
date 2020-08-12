package com.xyz.cloud.sample.retry;

import com.xyz.cloud.retry.RetryableEvent;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
public class SampleEvent extends RetryableEvent {
    public SampleEvent(@NotNull String data, @NotNull String traceId) {
        super(data, traceId);
    }

}
