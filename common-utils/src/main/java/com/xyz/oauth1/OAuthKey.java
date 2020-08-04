package com.xyz.oauth1;

import lombok.Data;

@Data
public class OAuthKey {
    private String appId;
    private String serviceName;
    private String key;
    private String secret;
    private String desc;
}
