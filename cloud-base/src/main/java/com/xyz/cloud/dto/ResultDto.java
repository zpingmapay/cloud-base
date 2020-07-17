package com.xyz.cloud.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class ResultDto<T> {

    private int code;

    private String msg;

    private T data;

    public ResultDto(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public boolean resultOk() {
        return HttpStatus.OK.value() == this.code;
    }

    public static <T> ResultDto<T> ok() {
        return new ResultDto<>(HttpStatus.OK.value(), "OK", null);
    }

    public static <T> ResultDto<T> ok(T data) {
        return new ResultDto<>(HttpStatus.OK.value(), "OK", data);
    }

    public static <T> ResultDto<T> ok(String msg, T data) {
        return new ResultDto<>(HttpStatus.OK.value(), msg, data);
    }

    public static <T> ResultDto<T> error(String msg) {
        return new ResultDto<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg, null);
    }

    public static <T> ResultDto<T> error(int code, String msg) {
        return new ResultDto<>(code, msg, null);
    }

    public static <T> ResultDto<T> error(int code, String msg, T data) {
        return new ResultDto<>(code, msg, data);
    }

}
