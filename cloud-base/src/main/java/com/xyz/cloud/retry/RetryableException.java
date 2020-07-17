package com.xyz.cloud.retry;

import com.xyz.exception.CommonException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RetryableException extends CommonException {
    public RetryableException(String msg) {
        this(null, msg);
    }

    public RetryableException(String code, String msg) {
        super(code, msg);
    }

    public RetryableException(String code, String msg, Throwable t) {
        super(code, msg, t);
    }
}
