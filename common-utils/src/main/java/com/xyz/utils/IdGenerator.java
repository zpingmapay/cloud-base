package com.xyz.utils;

import java.security.SecureRandom;
import java.util.Random;

public class IdGenerator {
    public static char[] symbols = new char[]{'a', 'b', 'c', 'd', 'e', 'f',
            'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z'};

    public static String randomDigits(int len) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    public static String randomChars(int len) {
        Random secureRandomProvider = new SecureRandom();
        char[] buffer = new char[len];
        for (int idx = 0; idx < buffer.length; ++idx)
            buffer[idx] = symbols[secureRandomProvider.nextInt(symbols.length)];
        return new String(buffer);
    }
}
