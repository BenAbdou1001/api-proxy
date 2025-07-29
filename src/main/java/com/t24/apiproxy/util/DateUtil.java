package com.t24.apiproxy.util;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    public static String nowIso() {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
    }
}
