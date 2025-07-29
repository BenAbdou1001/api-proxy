package com.t24.apiproxy.util;

public class StringUtil {
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
