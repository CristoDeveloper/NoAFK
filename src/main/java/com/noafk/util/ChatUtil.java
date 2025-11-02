package com.noafk.util;

public final class ChatUtil {
    private ChatUtil() {}

    public static String color(String s) {
        if (s == null) return "";
        return s.replace('&', '§');
    }
}