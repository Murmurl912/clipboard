package com.example.clipboard.server.helper;

public class RandomHelper {
    public static final char[] pools = "qwertyuiopasdfghjklzxcvbnm1234567890".toCharArray();

    public static String randomCode(int len) {
        char[] chars = new char[len];
        for(int i = 0; i < len; i++) {
            int index = (int)Math.floor(Math.floor(Math.abs(Math.random() * pools.length)));
            chars[i] = pools[index];
        }
        return new String(chars);
    }
}
