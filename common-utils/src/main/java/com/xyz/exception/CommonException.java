package com.xyz.exception;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.text.MessageFormat;

@Data
@NoArgsConstructor
public class CommonException extends RuntimeException implements Serializable {
    private final static String MSG_TEMPLATE = "Code:{0}, Msg:{1}, Err:{2}";
    private String code;
    private String msg;

    public CommonException(String code, String msg) {
        this(code, msg, null);
    }

    public CommonException(String code, String msg, Throwable cause) {
        super(getExceptionMessage(code, msg, cause), cause);
        this.code = code;
        this.msg = msg;
    }

    private static String getExceptionMessage(String code, String message, Throwable cause) {
        String extraMessage = "";
        if (null != cause && StringUtils.isNotBlank(cause.getMessage())) {
            extraMessage = cause.getMessage();
        }
        return MessageFormat.format(MSG_TEMPLATE, code, message, extraMessage);
    }
}
