package com.xyz.cloud.sample.controller;

import com.xyz.cloud.dto.ResultDto;
import com.xyz.cloud.jwt.JwtTokenProvider;
import com.xyz.cloud.jwt.annotation.JwtSecured;
import com.xyz.cloud.oauth1.annotation.OAuth1Secured;
import com.xyz.cloud.sample.retry.SampleEvent;
import com.xyz.cloud.sample.retry.SampleEventPublisher;
import com.xyz.cloud.sample.service.SampleRemoteService;
import com.xyz.cloud.trace.holder.DomainHeadersHolder;
import com.xyz.cloud.trace.holder.HttpHeadersHolder;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

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
    private SampleEventPublisher eventPublisher;
    @Resource
    @Qualifier(("taskExecutor"))
    private Executor executor;
    @Resource
    SampleRemoteService remoteService;

    @PostMapping("/login")
    public ResultDto<String> login() {
        String userId = getUserId();
        DomainHeadersHolder.DomainHeader headerObject = httpHeadersHolder.getHeaderObject();

        executor.execute(()-> log.info("user {} login", userId));
        eventPublisher.publish(new SampleEvent(userId, headerObject.getTraceId()));
        return ResultDto.ok(jwtTokenProvider.buildJwtToken(userId));
    }

    @JwtSecured
    @GetMapping("/")
    public ResultDto<String> doGet() {
        return ResultDto.ok("get ok");
    }

    @JwtSecured
    @PostMapping("/")
    public ResultDto<String> doPost() throws Exception {
        DomainHeadersHolder.DomainHeader headerObject = httpHeadersHolder.getHeaderObject();
        String userId = headerObject.getUserId();
        remoteService.echo(userId);
        remoteService.failedToObtainLock();
        ValidationUtils.isTrue(userId.equals(this.getUserId()), "Incorrect user id found");
        log.info("do post");
        eventPublisher.publish(new SampleEvent(userId, headerObject.getTraceId()));
        return ResultDto.ok("post ok");
    }

    @OAuth1Secured
    @GetMapping("/me")
    public ResultDto<String> myUserId(@RequestParam String userName) {
        ValidationUtils.notBlank(userName, "user name is required");
        return ResultDto.ok(getUserId());
    }

    private String getUserId() {
        return "123";
    }
}