package com.xyz.cloud.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

public class Status {
    @Getter
    @RequiredArgsConstructor
    public enum EntityStatus {
        INACTIVE(0, "有效"),
        ACTIVE(1, "无效");

        private final int code;
        private final String desc;

        public static EntityStatus of(int code) {
            return Arrays.stream(EntityStatus.values())
                    .filter(x -> code == x.getCode())
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown entity status " + code));
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum TxStatus {
        Init(0, "已提交"),
        Success(1, "已完成"),
        Failed(2, "已失败");

        private final int code;
        private final String desc;

        public static TxStatus of(int code) {
            return Arrays.stream(TxStatus.values())
                    .filter(x -> code == x.getCode())
                    .findAny()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown tx status " + code));
        }
    }
}
