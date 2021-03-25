package com.xyz.cloud.sample.controller;

import com.xyz.cloud.jwt.JwtTokenFactory;
import org.springframework.stereotype.Repository;

@Repository("SampleJwtTokenFactory")
public class SampleJwtTokenFactory implements JwtTokenFactory {
    @Override
    public String findByKey(String key) {
        return key;
    }
}
