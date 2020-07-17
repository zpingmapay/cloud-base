package com.xyz.cloud.jwt;

import lombok.Data;

import java.util.Base64;

@Data
public class JwtConfig {
    private String appId;
    private String secretSeed;
    private int ttlInHours;
    private String secret;

    public void setSecretSeed(String secretSeed) {
        this.secretSeed = secretSeed;
        this.secret = Base64.getEncoder().encodeToString(secretSeed.getBytes());
    }
}
