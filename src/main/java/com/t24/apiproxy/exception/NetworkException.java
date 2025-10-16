package com.t24.apiproxy.exception;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Exception thrown when network-related errors occur during API calls.
 * This includes connection failures, timeouts, HTTP errors, and other network issues.
 */
public class NetworkException extends ApiProxyException {
    
    private final String url;
    private final String method;
    private final Integer statusCode;
    private final String responseBody;
    private final boolean retryable;
    private final Duration retryAfter;
    private final int attemptNumber;
    private final long requestDuration;
    private final NetworkErrorType errorType;
    
    // HTTP status codes that are retryable
    private static final Set<Integer> RETRYABLE_STATUS_CODES = new HashSet<>(Arrays.asList(
        408, // Request Timeout
        425, // Too Early
        429, // Too Many Requests
        500, // Internal Server Error
        502, // Bad Gateway
        503, // Service Unavailable
        504  // Gateway Timeout
    ));
    
    /**
     * Creates a new NetworkException
     * 
     * @param errorCode Error code
     * @param userMessage User-friendly error message
     * @param technicalMessage Technical details for debugging
     * @param cause The underlying cause
     */
    public NetworkException(String errorCode, String userMessage, String technicalMessage, Throwable cause) {
        super(errorCode, userMessage, technicalMessage, cause);
        this.url = null;
        this.method = null;
        this.statusCode = null;
        this.responseBody = null;
        this.retryable = false;
        this.retryAfter = null;
        this.attemptNumber = 1;
        this.requestDuration = 0;
        this.errorType = NetworkErrorType.UNKNOWN;
    }
    
    /**
     * Creates a new NetworkException without a cause
     * 
     * @param errorCode Error code
     * @param userMessage User-friendly error message
     * @param technicalMessage Technical details for debugging
     */
    public NetworkException(String errorCode, String userMessage, String technicalMessage) {
        super(errorCode, userMessage, technicalMessage);
        this.url = null;
        this.method = null;
        this.statusCode = null;
        this.responseBody = null;
        this.retryable = false;
        this.retryAfter = null;
        this.attemptNumber = 1;
        this.requestDuration = 0;
        this.errorType = NetworkErrorType.UNKNOWN;
    }
    
    /**
     * Private constructor for builder
     */
    private NetworkException(Builder builder) {
        super(
            builder.errorCode != null ? builder.errorCode : "NETWORK_ERROR",
            builder.userMessage,
            builder.technicalMessage,
            builder.cause
        );
        this.url = builder.url;
        this.method = builder.method;
        this.statusCode = builder.statusCode;
        this.responseBody = builder.responseBody;
        this.retryable = builder.retryable;
        this.retryAfter = builder.retryAfter;
        this.attemptNumber = builder.attemptNumber;
        this.requestDuration = builder.requestDuration;
        this.errorType = builder.errorType;
        
        // Add context
        if (url != null) {
            addContext("url", url);
            withUrl(url);
        }
        if (method != null) addContext("method", method);
        if (statusCode != null) {
            addContext("statusCode", statusCode);
            withStatusCode(statusCode);
        }
        if (responseBody != null) addContext("responseBody", truncateResponseBody(responseBody));
        addContext("retryable", retryable);
        if (retryAfter != null) addContext("retryAfter", retryAfter.getSeconds() + "s");
        addContext("attemptNumber", attemptNumber);
        addContext("requestDuration", requestDuration + "ms");
        addContext("errorType", errorType.toString());
    }
    
    // Getters
    
    public String getUrl() {
        return url;
    }
    
    public String getMethod() {
        return method;
    }
    
    public Integer getStatusCode() {
        return statusCode;
    }
    
    public String getResponseBody() {
        return responseBody;
    }
    
    public Duration getRetryAfter() {
        return retryAfter;
    }
    
    public int getAttemptNumber() {
        return attemptNumber;
    }
    
    public long getRequestDuration() {
        return requestDuration;
    }
    
    public NetworkErrorType getErrorType() {
        return errorType;
    }
    
    @Override
    public boolean isRetryable() {
        return retryable;
    }
    
    @Override
    public Severity getSeverity() {
        if (statusCode != null) {
            if (statusCode >= 500) return Severity.ERROR;
            if (statusCode == 429) return Severity.WARNING;
            if (statusCode >= 400) return Severity.WARNING;
        }
        
        switch (errorType) {
            case TIMEOUT:
            case CONNECTION_REFUSED:
            case SERVICE_UNAVAILABLE:
                return Severity.ERROR;
            case RATE_LIMIT:
                return Severity.WARNING;
            case SSL_ERROR:
            case DNS_ERROR:
                return Severity.CRITICAL;
            default:
                return Severity.ERROR;
        }
    }
    
    /**
     * Checks if the status code is retryable
     */
    public static boolean isRetryableStatusCode(int statusCode) {
        return RETRYABLE_STATUS_CODES.contains(statusCode);
    }
    
    /**
     * Checks if this is a client error (4xx)
     */
    public boolean isClientError() {
        return statusCode != null && statusCode >= 400 && statusCode < 500;
    }
    
    /**
     * Checks if this is a server error (5xx)
     */
    public boolean isServerError() {
        return statusCode != null && statusCode >= 500 && statusCode < 600;
    }
    
    /**
     * Checks if this is a timeout error
     */
    public boolean isTimeout() {
        return errorType == NetworkErrorType.TIMEOUT || statusCode == 408 || statusCode == 504;
    }
    
    /**
     * Checks if this is a rate limit error
     */
    public boolean isRateLimit() {
        return errorType == NetworkErrorType.RATE_LIMIT || statusCode == 429;
    }
    
    /**
     * Truncates response body for logging
     */
    private static String truncateResponseBody(String body) {
        if (body == null) return null;
        if (body.length() <= 500) return body;
        return body.substring(0, 500) + "... [truncated]";
    }
    
    // Static factory methods
    
    /**
     * Creates a NetworkException for HTTP errors
     */
    public static NetworkException forHttpError(String url, String method, int statusCode, String responseBody) {
        NetworkErrorType errorType = classifyHttpError(statusCode);
        boolean retryable = isRetryableStatusCode(statusCode);
        
        return new Builder()
            .withErrorCode("HTTP_ERROR_" + statusCode)
            .withUrl(url)
            .withMethod(method)
            .withStatusCode(statusCode)
            .withResponseBody(responseBody)
            .withRetryable(retryable)
            .withErrorType(errorType)
            .withUserMessage("HTTP " + statusCode + " error")
            .withTechnicalMessage("HTTP " + method + " request to " + url + " returned status " + statusCode)
            .build();
    }
    
    /**
     * Creates a NetworkException for connection errors
     */
    public static NetworkException forConnectionError(String url, String method, Throwable cause) {
        return new Builder()
            .withErrorCode("CONNECTION_ERROR")
            .withUrl(url)
            .withMethod(method)
            .withRetryable(true)
            .withErrorType(NetworkErrorType.CONNECTION_REFUSED)
            .withUserMessage("Connection failed")
            .withTechnicalMessage("Failed to connect to " + url + ": " + cause.getMessage())
            .withCause(cause)
            .build();
    }
    
    /**
     * Creates a NetworkException for timeout errors
     */
    public static NetworkException forTimeout(String url, String method, long duration) {
        return new Builder()
            .withErrorCode("TIMEOUT")
            .withUrl(url)
            .withMethod(method)
            .withRequestDuration(duration)
            .withRetryable(true)
            .withErrorType(NetworkErrorType.TIMEOUT)
            .withUserMessage("Request timeout")
            .withTechnicalMessage("Request to " + url + " timed out after " + duration + "ms")
            .build();
    }
    
    /**
     * Creates a NetworkException for SSL errors
     */
    public static NetworkException forSslError(String url, Throwable cause) {
        return new Builder()
            .withErrorCode("SSL_ERROR")
            .withUrl(url)
            .withRetryable(false)
            .withErrorType(NetworkErrorType.SSL_ERROR)
            .withUserMessage("SSL/TLS error")
            .withTechnicalMessage("SSL error connecting to " + url + ": " + cause.getMessage())
            .withCause(cause)
            .build();
    }
    
    /**
     * Creates a NetworkException for rate limiting
     */
    public static NetworkException forRateLimit(String url, String method, Duration retryAfter) {
        return new Builder()
            .withErrorCode("RATE_LIMIT")
            .withUrl(url)
            .withMethod(method)
            .withStatusCode(429)
            .withRetryable(true)
            .withRetryAfter(retryAfter)
            .withErrorType(NetworkErrorType.RATE_LIMIT)
            .withUserMessage("Rate limit exceeded")
            .withTechnicalMessage("Rate limit exceeded for " + url + ". Retry after " + retryAfter.getSeconds() + " seconds")
            .build();
    }
    
    /**
     * Creates a NetworkException for DNS errors
     */
    public static NetworkException forDnsError(String url, Throwable cause) {
        return new Builder()
            .withErrorCode("DNS_ERROR")
            .withUrl(url)
            .withRetryable(false)
            .withErrorType(NetworkErrorType.DNS_ERROR)
            .withUserMessage("DNS resolution failed")
            .withTechnicalMessage("Failed to resolve hostname for " + url + ": " + cause.getMessage())
            .withCause(cause)
            .build();
    }
    
    /**
     * Creates a NetworkException for proxy errors
     */
    public static NetworkException forProxyError(String url, String proxyHost, Throwable cause) {
        return new Builder()
            .withErrorCode("PROXY_ERROR")
            .withUrl(url)
            .withRetryable(false)
            .withErrorType(NetworkErrorType.PROXY_ERROR)
            .withUserMessage("Proxy connection failed")
            .withTechnicalMessage("Failed to connect through proxy " + proxyHost + ": " + cause.getMessage())
            .withCause(cause)
            .build();
    }
    
    /**
     * Classifies HTTP error by status code
     */
    private static NetworkErrorType classifyHttpError(int statusCode) {
        if (statusCode == 429) return NetworkErrorType.RATE_LIMIT;
        if (statusCode == 408 || statusCode == 504) return NetworkErrorType.TIMEOUT;
        if (statusCode == 503) return NetworkErrorType.SERVICE_UNAVAILABLE;
        if (statusCode >= 500) return NetworkErrorType.SERVER_ERROR;
        if (statusCode >= 400) return NetworkErrorType.CLIENT_ERROR;
        return NetworkErrorType.UNKNOWN;
    }
    
    /**
     * Builder for creating NetworkException instances
     */
    public static class Builder {
        private String errorCode;
        private String userMessage;
        private String technicalMessage;
        private Throwable cause;
        private String url;
        private String method;
        private Integer statusCode;
        private String responseBody;
        private boolean retryable = false;
        private Duration retryAfter;
        private int attemptNumber = 1;
        private long requestDuration = 0;
        private NetworkErrorType errorType = NetworkErrorType.UNKNOWN;
        
        public Builder withErrorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }
        
        public Builder withUserMessage(String userMessage) {
            this.userMessage = userMessage;
            return this;
        }
        
        public Builder withTechnicalMessage(String technicalMessage) {
            this.technicalMessage = technicalMessage;
            return this;
        }
        
        public Builder withCause(Throwable cause) {
            this.cause = cause;
            return this;
        }
        
        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }
        
        public Builder withMethod(String method) {
            this.method = method;
            return this;
        }
        
        public Builder withStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
            return this;
        }
        
        public Builder withResponseBody(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }
        
        public Builder withRetryable(boolean retryable) {
            this.retryable = retryable;
            return this;
        }
        
        public Builder withRetryAfter(Duration retryAfter) {
            this.retryAfter = retryAfter;
            return this;
        }
        
        public Builder withAttemptNumber(int attemptNumber) {
            this.attemptNumber = attemptNumber;
            return this;
        }
        
        public Builder withRequestDuration(long requestDuration) {
            this.requestDuration = requestDuration;
            return this;
        }
        
        public Builder withErrorType(NetworkErrorType errorType) {
            this.errorType = errorType;
            return this;
        }
        
        public NetworkException build() {
            return new NetworkException(this);
        }
    }
    
    /**
     * Types of network errors
     */
    public enum NetworkErrorType {
        TIMEOUT,                // Request timeout
        CONNECTION_REFUSED,     // Connection refused
        DNS_ERROR,              // DNS resolution failed
        SSL_ERROR,              // SSL/TLS error
        PROXY_ERROR,            // Proxy connection error
        RATE_LIMIT,             // Rate limit exceeded
        CLIENT_ERROR,           // HTTP 4xx error
        SERVER_ERROR,           // HTTP 5xx error
        SERVICE_UNAVAILABLE,    // Service unavailable (503)
        UNKNOWN                 // Unknown error
    }
}
