package com.xyz.cloud.jwt;

public interface JwtTokenFactory {
    String findByKey(String key);
}
