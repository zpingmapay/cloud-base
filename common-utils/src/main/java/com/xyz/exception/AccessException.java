package com.xyz.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class AccessException extends CommonException {
    public AccessException(String msg) {
        this(null, msg);
    }

    public AccessException(String code, String msg) {
        super(code, msg);
    }

    public AccessException(String code, String msg, Throwable t) {
        super(code, msg, t);
    }

}
