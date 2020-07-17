package com.xyz.cloud.sample.controller;


import com.xyz.cloud.jwt.JwtTokenProvider;
import com.xyz.cloud.jwt.annotation.Jwt;
import com.xyz.cloud.log.holder.DomainHeadersHolder;
import com.xyz.cloud.log.holder.HttpHeadersHolder;
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
    public String login() {
        String userId = getUserId();
        return jwtTokenProvider.buildJwtToken(userId);
    }

    @Jwt
    @GetMapping("/")
    public String doGet() {
        return "get ok";
    }

    @Jwt
    @PostMapping("/")
    public String doPost() {
        DomainHeadersHolder.DomainHeader headerObject = (DomainHeadersHolder.DomainHeader)httpHeadersHolder.getHeaderObject();
        ValidationUtils.isTrue(headerObject.getUserId().equals(this.getUserId()), "Incorrect user id found");
        return "post ok";
    }

    private String getUserId() {
        return "123";
    }
}