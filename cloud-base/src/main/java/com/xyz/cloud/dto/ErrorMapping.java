package com.xyz.cloud.dto;

import com.xyz.utils.JsonConfig;
import lombok.Data;

import java.util.List;

public class ErrorMapping {
    private static final String ERROR_MAPPING_FILE = "error-mapping.json";
    private static final List<ErrorCode> errorCodes = JsonConfig.config2List(ERROR_MAPPING_FILE, ErrorCode.class);

    public static int mapErrorCode(int code) {
        return errorCodes.stream().filter(x -> x.getHttpCode() == code).map(ErrorCode::getSysCode).findAny().orElse(code);
    }

    public static String getErrorMsg(int code, String msg) {
        return errorCodes.stream().filter(x -> x.getHttpCode() == code).map(ErrorCode::getMsg).findAny().orElse(msg);
    }

    @Data
    public static class ErrorCode {
        private int httpCode;
        private int sysCode;
        private String msg;
    }
}
