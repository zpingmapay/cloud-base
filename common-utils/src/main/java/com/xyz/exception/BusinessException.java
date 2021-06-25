package com.xyz.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BusinessException extends CommonException {
    public BusinessException(String msg) {
        this(null, msg);
    }

    public BusinessException(String code, String msg) {
        super(code, msg);
    }

    public BusinessException(String code, String msg, Throwable t) {
        super(code, msg, t);
    }
}
