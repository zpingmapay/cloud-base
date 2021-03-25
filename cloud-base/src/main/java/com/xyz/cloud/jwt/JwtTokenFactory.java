package com.xyz.cloud.jwt;

public interface JwtTokenFactory {
    String findByKey(String key);

    class DefaultJwtTokenFactory implements JwtTokenFactory {

        @Override
        public String findByKey(String key) {
            return key;
        }
    }
}
