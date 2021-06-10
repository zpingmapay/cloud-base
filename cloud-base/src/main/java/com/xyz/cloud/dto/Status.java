package com.xyz.cloud.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Status {
    INACTIVE(0, "有效"),
    ACTIVE(1, "无效");

    private final int code;
    private final String desc;

    public static Status of(int code) {
        return Arrays.stream(Status.values())
                .filter(x -> code == x.getCode())
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Unknown status " + code));
    }
}
