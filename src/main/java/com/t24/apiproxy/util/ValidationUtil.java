package com.t24.apiproxy.util;

public class ValidationUtil {
    public static boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        String regex = "^(https?|ftp)://[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(:[0-9]+)?(/.*)?$";
        return url.matches(regex);
        
    }
}
