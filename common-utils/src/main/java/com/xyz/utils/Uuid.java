package com.xyz.utils;

import java.util.UUID;

public class Uuid {
    public static String generate() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
