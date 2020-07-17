package com.xyz.cloud.sample.controller;


import com.xyz.cloud.jwt.JwtTokenProvider;
import com.xyz.cloud.jwt.annotation.Jwt;
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
        return "post ok";
    }

    private String getUserId() {
        return "123";
    }
}