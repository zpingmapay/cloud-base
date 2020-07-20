package com.xyz.cloud.sample.controller;

import com.xyz.cloud.dto.ResultDto;
import com.xyz.cloud.jwt.JwtTokenProvider;
import com.xyz.cloud.jwt.annotation.Jwt;
import com.xyz.cloud.log.holder.DomainHeadersHolder;
import com.xyz.cloud.log.holder.HttpHeadersHolder;
import com.xyz.cloud.oauth1.annotation.OAuth1;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
public class SampleController {
    @Resource
    private JwtTokenProvider jwtTokenProvider;
    @Resource
    private HttpHeadersHolder httpHeadersHolder;

    @PostMapping("/login")
    public ResultDto<String> login() {
        String userId = getUserId();
        return ResultDto.ok(jwtTokenProvider.buildJwtToken(userId));
    }

    @Jwt
    @GetMapping("/")
    public ResultDto<String> doGet() {
        return ResultDto.ok("get ok");
    }

    @Jwt
    @PostMapping("/")
    public ResultDto<String> doPost() {
        DomainHeadersHolder.DomainHeader headerObject = (DomainHeadersHolder.DomainHeader)httpHeadersHolder.getHeaderObject();
        ValidationUtils.isTrue(headerObject.getUserId().equals(this.getUserId()), "Incorrect user id found");
        return ResultDto.ok("post ok");
    }

    @OAuth1
    @GetMapping("/me")
    public ResultDto<String> myUserId() {
        return ResultDto.ok(getUserId());
    }

    private String getUserId() {
        return "123";
    }
}