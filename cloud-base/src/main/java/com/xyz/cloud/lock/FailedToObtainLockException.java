package com.xyz.cloud.lock;

import com.xyz.exception.CommonException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FailedToObtainLockException extends CommonException {
    public FailedToObtainLockException(String msg) {
        this(null, msg);
    }

    public FailedToObtainLockException(String code, String msg) {
        super(code, msg);
    }

    public FailedToObtainLockException(String code, String msg, Throwable t) {
        super(code, msg, t);
    }
}
