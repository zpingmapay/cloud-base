package com.xyz.cloud.exceptionhandler;

import com.xyz.cloud.dto.ResultDto;
import com.xyz.cloud.lock.FailedToObtainLockException;
import com.xyz.cloud.trace.holder.HttpHeadersHolder;
import com.xyz.exception.AccessException;
import com.xyz.exception.CommonException;
import com.xyz.exception.ValidationException;
import com.xyz.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@ControllerAdvice(annotations = RestController.class)
public class DefaultGlobalExceptionHandler {
    @Resource
    private HttpHeadersHolder httpHeadersHolder;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultDto handleExceptionRequest(Exception e) {
        log.error("System exception, herders: {}", JsonUtils.beanToJson(httpHeadersHolder.getHeaderObject()), e);
        return ResultDto.error(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultDto handleExceptionRequest(MethodArgumentNotValidException e) {
        log.error("Validation exception, herders: {}", JsonUtils.beanToJson(httpHeadersHolder.getHeaderObject()), e);
        String message = "Invalid Parameters";
        for (ObjectError o : e.getBindingResult().getAllErrors()) {
            message = o.getDefaultMessage();
            break;
        }
        return ResultDto.error(HttpStatus.BAD_REQUEST.value(), "Bad request");
    }

    @ExceptionHandler(CommonException.class)
    @ResponseBody
    public ResultDto handleExceptionRequest(CommonException e) {
        log.error("System exception, herders: {}", JsonUtils.beanToJson(httpHeadersHolder.getHeaderObject()), e);
        return ResultDto.error(e.getMessage());
    }

    @ExceptionHandler(FailedToObtainLockException.class)
    @ResponseBody
    public ResultDto handleExceptionRequest(FailedToObtainLockException e) {
        return ResultDto.error(HttpStatus.NOT_ACCEPTABLE.value(), "Please wait a while");
    }

    @ExceptionHandler(AccessException.class)
    @ResponseBody
    public ResultDto handleExceptionRequest(AccessException e) {
        log.error("Access exception, herders: {}", JsonUtils.beanToJson(httpHeadersHolder.getHeaderObject()), e);
        return ResultDto.error(HttpStatus.UNAUTHORIZED.value(), "Invalid access token");
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public ResultDto handleExceptionRequest(ValidationException e) {
        log.error("Validation exception, herders: {}", JsonUtils.beanToJson(httpHeadersHolder.getHeaderObject()), e);
        return ResultDto.error(e.getMsg());
    }
}

