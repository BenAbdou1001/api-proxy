package com.t24.apiproxy.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * Utility class for date and time operations
 */
public class DateUtil {
    
    // Common date/time formatters
    public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_INSTANT;
    public static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    public static final DateTimeFormatter ISO_DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    public static final DateTimeFormatter RFC_1123_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME;
    
    // Custom formatters
    public static final DateTimeFormatter READABLE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter TIMESTAMP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    public static final DateTimeFormatter LOG_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /**
     * Gets current timestamp in ISO-8601 format
     * 
     * @return Current timestamp as ISO string
     */
    public static String nowIso() {
        return ISO_FORMATTER.format(Instant.now());
    }
    
    /**
     * Gets current timestamp in milliseconds since epoch
     * 
     * @return Current timestamp in milliseconds
     */
    public static long nowMillis() {
        return System.currentTimeMillis();
    }
    
    /**
     * Gets current timestamp in seconds since epoch
     * 
     * @return Current timestamp in seconds
     */
    public static long nowSeconds() {
        return Instant.now().getEpochSecond();
    }
    
    /**
     * Gets current date/time in readable format (yyyy-MM-dd HH:mm:ss)
     * 
     * @return Current date/time as readable string
     */
    public static String nowReadable() {
        return READABLE_FORMATTER.format(LocalDateTime.now());
    }
    
    /**
     * Gets current date/time suitable for log entries
     * 
     * @return Current date/time with milliseconds
     */
    public static String nowForLog() {
        return LOG_FORMATTER.format(LocalDateTime.now());
    }
    
    /**
     * Gets current date/time suitable for filenames
     * 
     * @return Current date/time as timestamp string
     */
    public static String nowForFilename() {
        return TIMESTAMP_FORMATTER.format(LocalDateTime.now());
    }
    
    /**
     * Formats a timestamp in milliseconds to ISO format
     * 
     * @param millis Timestamp in milliseconds
     * @return ISO formatted string
     */
    public static String formatMillisToIso(long millis) {
        return ISO_FORMATTER.format(Instant.ofEpochMilli(millis));
    }
    
    /**
     * Formats a timestamp in milliseconds to readable format
     * 
     * @param millis Timestamp in milliseconds
     * @return Readable formatted string
     */
    public static String formatMillisToReadable(long millis) {
        return READABLE_FORMATTER.format(
            LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault())
        );
    }
    
    /**
     * Formats an Instant to readable format
     * 
     * @param instant The Instant to format
     * @return Readable formatted string
     */
    public static String formatInstant(Instant instant) {
        if (instant == null) return null;
        return READABLE_FORMATTER.format(
            LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        );
    }
    
    /**
     * Parses an ISO-8601 date/time string to Instant
     * 
     * @param isoString ISO formatted date/time string
     * @return Parsed Instant or null if parsing fails
     */
    public static Instant parseIso(String isoString) {
        if (isoString == null || isoString.isEmpty()) {
            return null;
        }
        try {
            return Instant.parse(isoString);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Parses a date/time string using a custom pattern
     * 
     * @param dateString Date/time string to parse
     * @param pattern Pattern to use for parsing
     * @return Parsed Instant or null if parsing fails
     */
    public static Instant parseWithPattern(String dateString, String pattern) {
        if (dateString == null || dateString.isEmpty() || pattern == null) {
            return null;
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        } catch (DateTimeParseException e) {
            return null;
        }
    }
    
    /**
     * Calculates the duration between two timestamps in milliseconds
     * 
     * @param startMillis Start timestamp in milliseconds
     * @param endMillis End timestamp in milliseconds
     * @return Duration in milliseconds
     */
    public static long durationMillis(long startMillis, long endMillis) {
        return endMillis - startMillis;
    }
    
    /**
     * Calculates the duration between two Instants
     * 
     * @param start Start instant
     * @param end End instant
     * @return Duration between the two instants
     */
    public static Duration duration(Instant start, Instant end) {
        if (start == null || end == null) {
            return Duration.ZERO;
        }
        return Duration.between(start, end);
    }
    
    /**
     * Formats a duration in milliseconds to human-readable format
     * 
     * @param millis Duration in milliseconds
     * @return Human-readable duration string (e.g., "2h 30m 15s")
     */
    public static String formatDuration(long millis) {
        if (millis < 0) {
            return "0ms";
        }
        
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) {
            return String.format("%dd %dh %dm", days, hours % 24, minutes % 60);
        } else if (hours > 0) {
            return String.format("%dh %dm %ds", hours, minutes % 60, seconds % 60);
        } else if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds % 60);
        } else if (seconds > 0) {
            return String.format("%ds %dms", seconds, millis % 1000);
        } else {
            return millis + "ms";
        }
    }
    
    /**
     * Checks if a date string is valid ISO-8601 format
     * 
     * @param dateString Date string to validate
     * @return true if valid ISO-8601 format, false otherwise
     */
    public static boolean isValidIso(String dateString) {
        return parseIso(dateString) != null;
    }
    
    /**
     * Converts a Date to Instant
     * 
     * @param date Date to convert
     * @return Instant representation
     */
    public static Instant toInstant(Date date) {
        return date != null ? date.toInstant() : null;
    }
    
    /**
     * Converts an Instant to Date
     * 
     * @param instant Instant to convert
     * @return Date representation
     */
    public static Date toDate(Instant instant) {
        return instant != null ? Date.from(instant) : null;
    }
    
    /**
     * Gets the current date in ISO format (yyyy-MM-dd)
     * 
     * @return Current date as ISO string
     */
    public static String todayIso() {
        return ISO_DATE_FORMATTER.format(LocalDate.now());
    }
    
    /**
     * Adds milliseconds to a timestamp
     * 
     * @param millis Base timestamp
     * @param millisToAdd Milliseconds to add
     * @return New timestamp
     */
    public static long addMillis(long millis, long millisToAdd) {
        return millis + millisToAdd;
    }
    
    /**
     * Checks if a timestamp is in the past
     * 
     * @param millis Timestamp to check
     * @return true if timestamp is in the past
     */
    public static boolean isPast(long millis) {
        return millis < System.currentTimeMillis();
    }
    
    /**
     * Checks if a timestamp is in the future
     * 
     * @param millis Timestamp to check
     * @return true if timestamp is in the future
     */
    public static boolean isFuture(long millis) {
        return millis > System.currentTimeMillis();
    }
    
    /**
     * Gets a timestamp for X milliseconds from now
     * 
     * @param millisFromNow Milliseconds from now
     * @return Future timestamp
     */
    public static long fromNow(long millisFromNow) {
        return System.currentTimeMillis() + millisFromNow;
    }
    
    /**
     * Gets a timestamp for X milliseconds ago
     * 
     * @param millisAgo Milliseconds ago
     * @return Past timestamp
     */
    public static long ago(long millisAgo) {
        return System.currentTimeMillis() - millisAgo;
    }
}
