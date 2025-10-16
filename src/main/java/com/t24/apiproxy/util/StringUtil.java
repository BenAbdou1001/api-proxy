package com.t24.apiproxy.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for string operations
 */
public class StringUtil {
    
    /**
     * Checks if a string is null, empty, or contains only whitespace
     * 
     * @param s String to check
     * @return true if string is blank, false otherwise
     */
    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    
    /**
     * Checks if a string is not blank
     * 
     * @param s String to check
     * @return true if string is not blank, false otherwise
     */
    public static boolean isNotBlank(String s) {
        return !isBlank(s);
    }
    
    /**
     * Checks if a string is null or empty (doesn't trim)
     * 
     * @param s String to check
     * @return true if string is empty, false otherwise
     */
    public static boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }
    
    /**
     * Checks if a string is not empty
     * 
     * @param s String to check
     * @return true if string is not empty, false otherwise
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }
    
    /**
     * Returns the string or a default value if the string is blank
     * 
     * @param s String to check
     * @param defaultValue Default value to return if string is blank
     * @return Original string or default value
     */
    public static String defaultIfBlank(String s, String defaultValue) {
        return isBlank(s) ? defaultValue : s;
    }
    
    /**
     * Returns the string or empty string if null
     * 
     * @param s String to check
     * @return Original string or empty string
     */
    public static String defaultString(String s) {
        return s == null ? "" : s;
    }
    
    /**
     * Truncates a string to a maximum length
     * 
     * @param s String to truncate
     * @param maxLength Maximum length
     * @return Truncated string
     */
    public static String truncate(String s, int maxLength) {
        if (s == null || s.length() <= maxLength) {
            return s;
        }
        return s.substring(0, maxLength);
    }
    
    /**
     * Truncates a string to a maximum length and adds suffix
     * 
     * @param s String to truncate
     * @param maxLength Maximum length (including suffix)
     * @param suffix Suffix to add (e.g., "...")
     * @return Truncated string with suffix
     */
    public static String truncateWithSuffix(String s, int maxLength, String suffix) {
        if (s == null || s.length() <= maxLength) {
            return s;
        }
        int suffixLength = suffix != null ? suffix.length() : 0;
        return s.substring(0, maxLength - suffixLength) + defaultString(suffix);
    }
    
    /**
     * Repeats a string n times
     * 
     * @param s String to repeat
     * @param times Number of times to repeat
     * @return Repeated string
     */
    public static String repeat(String s, int times) {
        if (s == null || times <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length() * times);
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }
    
    /**
     * Pads a string on the left to a certain length
     * 
     * @param s String to pad
     * @param length Desired length
     * @param padChar Character to pad with
     * @return Padded string
     */
    public static String padLeft(String s, int length, char padChar) {
        if (s == null) s = "";
        if (s.length() >= length) return s;
        return repeat(String.valueOf(padChar), length - s.length()) + s;
    }
    
    /**
     * Pads a string on the right to a certain length
     * 
     * @param s String to pad
     * @param length Desired length
     * @param padChar Character to pad with
     * @return Padded string
     */
    public static String padRight(String s, int length, char padChar) {
        if (s == null) s = "";
        if (s.length() >= length) return s;
        return s + repeat(String.valueOf(padChar), length - s.length());
    }
    
    /**
     * Capitalizes the first character of a string
     * 
     * @param s String to capitalize
     * @return Capitalized string
     */
    public static String capitalize(String s) {
        if (isBlank(s)) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    /**
     * Converts first character to lowercase
     * 
     * @param s String to uncapitalize
     * @return Uncapitalized string
     */
    public static String uncapitalize(String s) {
        if (isBlank(s)) return s;
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }
    
    /**
     * Converts a string to camelCase
     * 
     * @param s String to convert
     * @return camelCase string
     */
    public static String toCamelCase(String s) {
        if (isBlank(s)) return s;
        String[] parts = s.split("[_\\s-]+");
        StringBuilder result = new StringBuilder(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            result.append(capitalize(parts[i].toLowerCase()));
        }
        return result.toString();
    }
    
    /**
     * Converts a string to snake_case
     * 
     * @param s String to convert
     * @return snake_case string
     */
    public static String toSnakeCase(String s) {
        if (isBlank(s)) return s;
        return s.replaceAll("([a-z])([A-Z])", "$1_$2")
                .replaceAll("[\\s-]+", "_")
                .toLowerCase();
    }
    
    /**
     * Converts a string to kebab-case
     * 
     * @param s String to convert
     * @return kebab-case string
     */
    public static String toKebabCase(String s) {
        if (isBlank(s)) return s;
        return s.replaceAll("([a-z])([A-Z])", "$1-$2")
                .replaceAll("[\\s_]+", "-")
                .toLowerCase();
    }
    
    /**
     * Reverses a string
     * 
     * @param s String to reverse
     * @return Reversed string
     */
    public static String reverse(String s) {
        if (s == null) return null;
        return new StringBuilder(s).reverse().toString();
    }
    
    /**
     * Counts occurrences of a substring in a string
     * 
     * @param s String to search in
     * @param substring Substring to count
     * @return Number of occurrences
     */
    public static int countOccurrences(String s, String substring) {
        if (isBlank(s) || isBlank(substring)) return 0;
        int count = 0;
        int index = 0;
        while ((index = s.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
    
    /**
     * Removes all whitespace from a string
     * 
     * @param s String to process
     * @return String without whitespace
     */
    public static String removeWhitespace(String s) {
        if (s == null) return null;
        return s.replaceAll("\\s+", "");
    }
    
    /**
     * Normalizes whitespace (replaces multiple spaces with single space)
     * 
     * @param s String to normalize
     * @return Normalized string
     */
    public static String normalizeWhitespace(String s) {
        if (s == null) return null;
        return s.trim().replaceAll("\\s+", " ");
    }
    
    /**
     * Masks a string by replacing characters with a mask character
     * 
     * @param s String to mask
     * @param visibleStart Number of characters to keep visible at start
     * @param visibleEnd Number of characters to keep visible at end
     * @param maskChar Character to use for masking
     * @return Masked string
     */
    public static String mask(String s, int visibleStart, int visibleEnd, char maskChar) {
        if (s == null || s.length() <= visibleStart + visibleEnd) {
            return s;
        }
        String start = s.substring(0, visibleStart);
        String end = s.substring(s.length() - visibleEnd);
        String masked = repeat(String.valueOf(maskChar), s.length() - visibleStart - visibleEnd);
        return start + masked + end;
    }
    
    /**
     * Encodes a string to Base64
     * 
     * @param s String to encode
     * @return Base64 encoded string
     */
    public static String toBase64(String s) {
        if (s == null) return null;
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Decodes a Base64 string
     * 
     * @param base64 Base64 encoded string
     * @return Decoded string
     */
    public static String fromBase64(String base64) {
        if (base64 == null) return null;
        try {
            byte[] decoded = Base64.getDecoder().decode(base64);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Generates MD5 hash of a string
     * 
     * @param s String to hash
     * @return MD5 hash as hex string
     */
    public static String md5(String s) {
        return hash(s, "MD5");
    }
    
    /**
     * Generates SHA-256 hash of a string
     * 
     * @param s String to hash
     * @return SHA-256 hash as hex string
     */
    public static String sha256(String s) {
        return hash(s, "SHA-256");
    }
    
    /**
     * Generates hash of a string using specified algorithm
     * 
     * @param s String to hash
     * @param algorithm Hash algorithm (e.g., "MD5", "SHA-256")
     * @return Hash as hex string
     */
    private static String hash(String s, String algorithm) {
        if (s == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(s.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
    
    /**
     * Converts bytes to hex string
     * 
     * @param bytes Byte array
     * @return Hex string
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    /**
     * Joins strings with a delimiter
     * 
     * @param delimiter Delimiter to use
     * @param elements Elements to join
     * @return Joined string
     */
    public static String join(String delimiter, String... elements) {
        if (elements == null || elements.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < elements.length; i++) {
            if (i > 0) sb.append(delimiter);
            sb.append(elements[i]);
        }
        return sb.toString();
    }
    
    /**
     * Splits a string by delimiter and trims each part
     * 
     * @param s String to split
     * @param delimiter Delimiter
     * @return Array of trimmed parts
     */
    public static String[] splitAndTrim(String s, String delimiter) {
        if (s == null) return new String[0];
        String[] parts = s.split(Pattern.quote(delimiter));
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }
    
    /**
     * Extracts all matches of a regex pattern from a string
     * 
     * @param s String to search
     * @param regex Regular expression pattern
     * @return List of matches
     */
    public static List<String> extractMatches(String s, String regex) {
        List<String> matches = new ArrayList<>();
        if (s == null || regex == null) return matches;
        
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            matches.add(matcher.group());
        }
        return matches;
    }
    
    /**
     * Checks if a string contains only digits
     * 
     * @param s String to check
     * @return true if string contains only digits
     */
    public static boolean isNumeric(String s) {
        if (isBlank(s)) return false;
        return s.matches("\\d+");
    }
    
    /**
     * Checks if a string contains only letters
     * 
     * @param s String to check
     * @return true if string contains only letters
     */
    public static boolean isAlpha(String s) {
        if (isBlank(s)) return false;
        return s.matches("[a-zA-Z]+");
    }
    
    /**
     * Checks if a string contains only letters and digits
     * 
     * @param s String to check
     * @return true if string contains only letters and digits
     */
    public static boolean isAlphanumeric(String s) {
        if (isBlank(s)) return false;
        return s.matches("[a-zA-Z0-9]+");
    }
    
    /**
     * Abbreviates a string in the middle
     * 
     * @param s String to abbreviate
     * @param maxLength Maximum length
     * @return Abbreviated string
     */
    public static String abbreviateMiddle(String s, int maxLength) {
        if (s == null || s.length() <= maxLength) {
            return s;
        }
        int halfLength = (maxLength - 3) / 2;
        return s.substring(0, halfLength) + "..." + s.substring(s.length() - halfLength);
    }
}
