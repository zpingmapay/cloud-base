package com.xyz.oauth1;

import lombok.Data;

@Data
public class OAuthKey {
    private String name;
    private String url;
    private String key;
    private String secret;
    private String desc;
}
