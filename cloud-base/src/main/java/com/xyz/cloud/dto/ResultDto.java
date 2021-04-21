package com.xyz.cloud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import static com.xyz.cloud.dto.ErrorMapping.getErrorMsg;
import static com.xyz.cloud.dto.ErrorMapping.mapErrorCode;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.OK;

@Data
@NoArgsConstructor
public class ResultDto<T> {
    private Integer code;

    private String msg;

    private T data;

    public ResultDto(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public boolean resultOk() {
        int mappedCode = mapErrorCode(OK.value());
        return mappedCode == this.code;
    }

    public static <T> ResultDto<T> ok() {
        int errorCode = mapErrorCode(OK.value());
        String errorMsg = getErrorMsg(OK.value(), "请求成功");
        return new ResultDto<>(errorCode, errorMsg, null);
    }

    public static <T> ResultDto<T> ok(T data) {
        int errorCode = mapErrorCode(OK.value());
        String errorMsg = getErrorMsg(OK.value(), "请求成功");
        return new ResultDto<>(errorCode, errorMsg, data);
    }

    public static <T> ResultDto<T> ok(String msg, T data) {
        int errorCode = mapErrorCode(OK.value());
        String errorMsg = getErrorMsg(OK.value(), msg);
        return new ResultDto<>(errorCode, errorMsg, data);
    }

    public static <T> ResultDto<T> error(String msg) {
        int errorCode = mapErrorCode(INTERNAL_SERVER_ERROR.value());
        return new ResultDto<>(errorCode, msg, null);
    }

    public static <T> ResultDto<T> error(Integer code, String msg) {
        int errorCode = mapErrorCode(code);
        String errorMsg = getErrorMsg(code, msg);
        return new ResultDto<>(errorCode, errorMsg, null);
    }

    public static <T> ResultDto<T> error(Integer code, String msg, T data) {
        int errorCode = mapErrorCode(code);
        String errorMsg = getErrorMsg(code, msg);
        return new ResultDto<>(errorCode, errorMsg, data);
    }

}
