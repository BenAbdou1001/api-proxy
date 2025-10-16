package com.t24.apiproxy.util;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * Utility class for various validation operations
 */
public class ValidationUtil {
    
    // Common regex patterns
    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?|ftp)://[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(:[0-9]+)?(/.*)?$"
    );
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private static final Pattern IPV4_PATTERN = Pattern.compile(
        "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.){3}(25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)$"
    );
    
    private static final Pattern IPV6_PATTERN = Pattern.compile(
        "^(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|" +
        "([0-9a-fA-F]{1,4}:){1,7}:|" +
        "([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|" +
        "([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|" +
        "([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|" +
        "([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|" +
        "([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|" +
        "[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|" +
        ":((:[0-9a-fA-F]{1,4}){1,7}|:)|" +
        "fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|" +
        "::(ffff(:0{1,4}){0,1}:){0,1}" +
        "((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}" +
        "(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|" +
        "([0-9a-fA-F]{1,4}:){1,4}:" +
        "((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}" +
        "(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))$"
    );
    
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[(]?[0-9]{1,4}[)]?[-\\s\\.]?[0-9]{1,9}$"
    );
    
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");
    
    /**
     * Validates a URL using regex pattern
     * 
     * @param url URL string to validate
     * @return true if URL is valid, false otherwise
     */
    public static boolean isValidUrl(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        return URL_PATTERN.matcher(url).matches();
    }
    
    /**
     * Validates a URL using Java's URL class for more thorough validation
     * 
     * @param url URL string to validate
     * @return true if URL is valid and well-formed, false otherwise
     */
    public static boolean isValidUrlStrict(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        try {
            URI.create(url).toURL();
            return true;
        } catch (IllegalArgumentException | MalformedURLException e) {
            return false;
        }
    }
    
    /**
     * Validates a URI
     * 
     * @param uri URI string to validate
     * @return true if URI is valid, false otherwise
     */
    public static boolean isValidUri(String uri) {
        if (uri == null || uri.isEmpty()) {
            return false;
        }
        try {
            new URI(uri);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
    
    /**
     * Validates an email address
     * 
     * @param email Email address to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates an IPv4 address
     * 
     * @param ip IP address to validate
     * @return true if IPv4 address is valid, false otherwise
     */
    public static boolean isValidIpv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return IPV4_PATTERN.matcher(ip).matches();
    }
    
    /**
     * Validates an IPv6 address
     * 
     * @param ip IP address to validate
     * @return true if IPv6 address is valid, false otherwise
     */
    public static boolean isValidIpv6(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        return IPV6_PATTERN.matcher(ip).matches();
    }
    
    /**
     * Validates an IP address (IPv4 or IPv6)
     * 
     * @param ip IP address to validate
     * @return true if IP address is valid, false otherwise
     */
    public static boolean isValidIp(String ip) {
        return isValidIpv4(ip) || isValidIpv6(ip);
    }
    
    /**
     * Validates an IP address using Java's InetAddress
     * 
     * @param ip IP address to validate
     * @return true if IP address is valid and reachable, false otherwise
     */
    public static boolean isValidIpStrict(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }
    
    /**
     * Validates a phone number
     * 
     * @param phone Phone number to validate
     * @return true if phone number is valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Validates a port number
     * 
     * @param port Port number to validate
     * @return true if port is valid (1-65535), false otherwise
     */
    public static boolean isValidPort(int port) {
        return port >= 1 && port <= 65535;
    }
    
    /**
     * Validates a port number from string
     * 
     * @param port Port number string to validate
     * @return true if port is valid, false otherwise
     */
    public static boolean isValidPort(String port) {
        if (port == null || port.isEmpty()) {
            return false;
        }
        try {
            int portNum = Integer.parseInt(port);
            return isValidPort(portNum);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates an alphanumeric string
     * 
     * @param str String to validate
     * @return true if string contains only letters and digits, false otherwise
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return ALPHANUMERIC_PATTERN.matcher(str).matches();
    }
    
    /**
     * Validates that a string is not null or empty
     * 
     * @param str String to validate
     * @return true if string is not null and not empty, false otherwise
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }
    
    /**
     * Validates that a string is not null, not empty, and not only whitespace
     * 
     * @param str String to validate
     * @return true if string is not blank, false otherwise
     */
    public static boolean isNotBlank(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Validates string length is within range
     * 
     * @param str String to validate
     * @param minLength Minimum length (inclusive)
     * @param maxLength Maximum length (inclusive)
     * @return true if length is within range, false otherwise
     */
    public static boolean isLengthInRange(String str, int minLength, int maxLength) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * Validates that a string matches a regex pattern
     * 
     * @param str String to validate
     * @param regex Regular expression pattern
     * @return true if string matches pattern, false otherwise
     */
    public static boolean matchesPattern(String str, String regex) {
        if (str == null || regex == null) {
            return false;
        }
        return str.matches(regex);
    }
    
    /**
     * Validates that a number is within a range
     * 
     * @param value Value to validate
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return true if value is within range, false otherwise
     */
    public static boolean isInRange(int value, int min, int max) {
        return value >= min && value <= max;
    }
    
    /**
     * Validates that a number is within a range
     * 
     * @param value Value to validate
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return true if value is within range, false otherwise
     */
    public static boolean isInRange(long value, long min, long max) {
        return value >= min && value <= max;
    }
    
    /**
     * Validates that a number is within a range
     * 
     * @param value Value to validate
     * @param min Minimum value (inclusive)
     * @param max Maximum value (inclusive)
     * @return true if value is within range, false otherwise
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
    
    /**
     * Validates that a number is positive
     * 
     * @param value Value to validate
     * @return true if value is positive (> 0), false otherwise
     */
    public static boolean isPositive(int value) {
        return value > 0;
    }
    
    /**
     * Validates that a number is positive
     * 
     * @param value Value to validate
     * @return true if value is positive (> 0), false otherwise
     */
    public static boolean isPositive(long value) {
        return value > 0;
    }
    
    /**
     * Validates that a number is positive
     * 
     * @param value Value to validate
     * @return true if value is positive (> 0), false otherwise
     */
    public static boolean isPositive(double value) {
        return value > 0;
    }
    
    /**
     * Validates that a number is non-negative
     * 
     * @param value Value to validate
     * @return true if value is non-negative (>= 0), false otherwise
     */
    public static boolean isNonNegative(int value) {
        return value >= 0;
    }
    
    /**
     * Validates that a number is non-negative
     * 
     * @param value Value to validate
     * @return true if value is non-negative (>= 0), false otherwise
     */
    public static boolean isNonNegative(long value) {
        return value >= 0;
    }
    
    /**
     * Validates that a number is non-negative
     * 
     * @param value Value to validate
     * @return true if value is non-negative (>= 0), false otherwise
     */
    public static boolean isNonNegative(double value) {
        return value >= 0;
    }
    
    /**
     * Validates JSON string format (basic check)
     * 
     * @param json JSON string to validate
     * @return true if string appears to be valid JSON, false otherwise
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        String trimmed = json.trim();
        return (trimmed.startsWith("{") && trimmed.endsWith("}")) ||
               (trimmed.startsWith("[") && trimmed.endsWith("]"));
    }
    
    /**
     * Validates XML string format (basic check)
     * 
     * @param xml XML string to validate
     * @return true if string appears to be valid XML, false otherwise
     */
    public static boolean isValidXml(String xml) {
        if (xml == null || xml.trim().isEmpty()) {
            return false;
        }
        String trimmed = xml.trim();
        return trimmed.startsWith("<") && trimmed.endsWith(">");
    }
    
    /**
     * Validates that an object is not null
     * 
     * @param obj Object to validate
     * @return true if object is not null, false otherwise
     */
    public static boolean isNotNull(Object obj) {
        return obj != null;
    }
    
    /**
     * Validates that all objects in array are not null
     * 
     * @param objects Objects to validate
     * @return true if all objects are not null, false otherwise
     */
    public static boolean areNotNull(Object... objects) {
        if (objects == null) {
            return false;
        }
        for (Object obj : objects) {
            if (obj == null) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Validates HTTP status code
     * 
     * @param statusCode Status code to validate
     * @return true if status code is valid (100-599), false otherwise
     */
    public static boolean isValidHttpStatusCode(int statusCode) {
        return statusCode >= 100 && statusCode <= 599;
    }
    
    /**
     * Validates HTTP method
     * 
     * @param method HTTP method to validate
     * @return true if method is valid, false otherwise
     */
    public static boolean isValidHttpMethod(String method) {
        if (method == null || method.isEmpty()) {
            return false;
        }
        String upperMethod = method.toUpperCase();
        return upperMethod.equals("GET") || 
               upperMethod.equals("POST") || 
               upperMethod.equals("PUT") || 
               upperMethod.equals("DELETE") || 
               upperMethod.equals("PATCH") || 
               upperMethod.equals("HEAD") || 
               upperMethod.equals("OPTIONS") || 
               upperMethod.equals("TRACE") ||
               upperMethod.equals("CONNECT");
    }
    
    /**
     * Validates content type format
     * 
     * @param contentType Content type to validate
     * @return true if content type is valid, false otherwise
     */
    public static boolean isValidContentType(String contentType) {
        if (contentType == null || contentType.isEmpty()) {
            return false;
        }
        // Basic validation: type/subtype
        return contentType.matches("^[a-zA-Z0-9][a-zA-Z0-9!#$&^_.-]*" +
                                   "/[a-zA-Z0-9][a-zA-Z0-9!#$&^_.-]*" +
                                   "(\\s*;\\s*.+)?$");
    }
}
