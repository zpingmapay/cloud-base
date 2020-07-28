package com.xyz.cloud.sample.controller;

import com.xyz.cloud.dto.ResultDto;
import com.xyz.cloud.jwt.JwtTokenProvider;
import com.xyz.cloud.jwt.annotation.JwtSecured;
import com.xyz.cloud.log.holder.DomainHeadersHolder;
import com.xyz.cloud.log.holder.HttpHeadersHolder;
import com.xyz.cloud.oauth1.annotation.OAuth1Secured;
import com.xyz.cloud.sample.retry.SampleEvent;
import com.xyz.cloud.sample.retry.SampleEventPublisher;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.Executor;

@Slf4j
@RestController
public class SampleController {
    @Resource
    private JwtTokenProvider jwtTokenProvider;
    @Resource
    private HttpHeadersHolder<DomainHeadersHolder.DomainHeader> httpHeadersHolder;
    @Resource
    private Executor executor;
    @Resource
    private SampleEventPublisher eventPublisher;

    @PostMapping("/login")
    public ResultDto<String> login() {
        String userId = getUserId();
        DomainHeadersHolder.DomainHeader headerObject = httpHeadersHolder.getHeaderObject();

        executor.execute(() -> {
            log.info("user {} login", userId);
            eventPublisher.publish(new SampleEvent(userId, headerObject.getTraceId()));
        });
        return ResultDto.ok(jwtTokenProvider.buildJwtToken(userId));
    }

    @JwtSecured
    @GetMapping("/")
    public ResultDto<String> doGet() {
        return ResultDto.ok("get ok");
    }

    @JwtSecured
    @PostMapping("/")
    public ResultDto<String> doPost() {
        ValidationUtils.isTrue(httpHeadersHolder.getString("user-id").equals(this.getUserId()), "Incorrect user id found");

        DomainHeadersHolder.DomainHeader headerObject = httpHeadersHolder.getHeaderObject();
        ValidationUtils.isTrue(headerObject.getUserId().equals(this.getUserId()), "Incorrect user id found");
        return ResultDto.ok("post ok");
    }

    @OAuth1Secured
    @GetMapping("/me")
    public ResultDto<String> myUserId() {
        return ResultDto.ok(getUserId());
    }

    private String getUserId() {
        return "123";
    }
}