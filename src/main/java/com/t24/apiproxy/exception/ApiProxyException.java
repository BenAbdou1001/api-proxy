package com.t24.apiproxy.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Base exception class for all API Proxy exceptions.
 * Provides structured error information with error codes, messages, and context.
 */
public class ApiProxyException extends RuntimeException {
    
    private final String errorCode;
    private final String userMessage;
    private final String technicalMessage;
    private final Instant timestamp;
    private final Map<String, Object> context;
    private String requestId;
    private String url;
    private Integer statusCode;
    
    /**
     * Creates a new ApiProxyException
     * 
     * @param errorCode Unique error code identifying the error type
     * @param userMessage User-friendly error message
     * @param technicalMessage Technical details for debugging
     */
    public ApiProxyException(String errorCode, String userMessage, String technicalMessage) {
        super(formatMessage(errorCode, userMessage));
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.technicalMessage = technicalMessage;
        this.timestamp = Instant.now();
        this.context = new HashMap<>();
    }
    
    /**
     * Creates a new ApiProxyException with a cause
     * 
     * @param errorCode Unique error code identifying the error type
     * @param userMessage User-friendly error message
     * @param technicalMessage Technical details for debugging
     * @param cause The underlying cause of this exception
     */
    public ApiProxyException(String errorCode, String userMessage, String technicalMessage, Throwable cause) {
        super(formatMessage(errorCode, userMessage), cause);
        this.errorCode = errorCode;
        this.userMessage = userMessage;
        this.technicalMessage = technicalMessage;
        this.timestamp = Instant.now();
        this.context = new HashMap<>();
    }
    
    /**
     * Formats the exception message
     */
    private static String formatMessage(String errorCode, String userMessage) {
        return String.format("[%s] %s", errorCode, userMessage);
    }
    
    // Getters
    
    /**
     * Gets the error code
     * @return Error code
     */
    public String getErrorCode() {
        return errorCode;
    }
    
    /**
     * Gets the user-friendly message
     * @return User message
     */
    public String getUserMessage() {
        return userMessage;
    }
    
    /**
     * Gets the technical message for debugging
     * @return Technical message
     */
    public String getTechnicalMessage() {
        return technicalMessage;
    }
    
    /**
     * Gets the timestamp when the exception was created
     * @return Timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the request ID associated with this exception
     * @return Request ID or null
     */
    public String getRequestId() {
        return requestId;
    }
    
    /**
     * Gets the URL associated with this exception
     * @return URL or null
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Gets the HTTP status code associated with this exception
     * @return Status code or null
     */
    public Integer getStatusCode() {
        return statusCode;
    }
    
    /**
     * Gets the context map
     * @return Context map
     */
    public Map<String, Object> getContext() {
        return context;
    }
    
    // Setters (fluent API)
    
    /**
     * Sets the request ID
     * @param requestId Request ID
     * @return This exception for chaining
     */
    public ApiProxyException withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
    
    /**
     * Sets the URL
     * @param url URL
     * @return This exception for chaining
     */
    public ApiProxyException withUrl(String url) {
        this.url = url;
        return this;
    }
    
    /**
     * Sets the HTTP status code
     * @param statusCode HTTP status code
     * @return This exception for chaining
     */
    public ApiProxyException withStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
        return this;
    }
    
    /**
     * Adds a context value
     * @param key Context key
     * @param value Context value
     * @return This exception for chaining
     */
    public ApiProxyException addContext(String key, Object value) {
        this.context.put(key, value);
        return this;
    }
    
    /**
     * Adds multiple context values
     * @param contextMap Map of context values
     * @return This exception for chaining
     */
    public ApiProxyException addContext(Map<String, Object> contextMap) {
        if (contextMap != null) {
            this.context.putAll(contextMap);
        }
        return this;
    }
    
    /**
     * Gets a context value
     * @param key Context key
     * @return Context value or null
     */
    public Object getContextValue(String key) {
        return context.get(key);
    }
    
    /**
     * Checks if exception has a specific context key
     * @param key Context key
     * @return true if context contains the key
     */
    public boolean hasContext(String key) {
        return context.containsKey(key);
    }
    
    /**
     * Checks if this exception is retryable
     * @return true if the operation can be retried
     */
    public boolean isRetryable() {
        // Override in subclasses for specific retry logic
        return false;
    }
    
    /**
     * Gets the severity level of this exception
     * @return Severity level
     */
    public Severity getSeverity() {
        // Override in subclasses for specific severity
        return Severity.ERROR;
    }
    
    /**
     * Converts the exception to a structured error response
     * @return Error response map
     */
    public Map<String, Object> toErrorResponse() {
        Map<String, Object> error = new HashMap<>();
        error.put("errorCode", errorCode);
        error.put("message", userMessage);
        error.put("timestamp", timestamp.toString());
        
        if (requestId != null) {
            error.put("requestId", requestId);
        }
        
        if (url != null) {
            error.put("url", url);
        }
        
        if (statusCode != null) {
            error.put("statusCode", statusCode);
        }
        
        if (!context.isEmpty()) {
            error.put("context", context);
        }
        
        return error;
    }
    
    /**
     * Converts the exception to a detailed error response (includes technical details)
     * @return Detailed error response map
     */
    public Map<String, Object> toDetailedErrorResponse() {
        Map<String, Object> error = toErrorResponse();
        error.put("technicalMessage", technicalMessage);
        error.put("severity", getSeverity().toString());
        error.put("retryable", isRetryable());
        
        if (getCause() != null) {
            error.put("cause", getCause().getClass().getSimpleName());
            error.put("causeMessage", getCause().getMessage());
        }
        
        return error;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName())
          .append("{errorCode='").append(errorCode).append('\'')
          .append(", userMessage='").append(userMessage).append('\'')
          .append(", technicalMessage='").append(technicalMessage).append('\'')
          .append(", timestamp=").append(timestamp);
        
        if (requestId != null) {
            sb.append(", requestId='").append(requestId).append('\'');
        }
        
        if (url != null) {
            sb.append(", url='").append(url).append('\'');
        }
        
        if (statusCode != null) {
            sb.append(", statusCode=").append(statusCode);
        }
        
        if (!context.isEmpty()) {
            sb.append(", context=").append(context);
        }
        
        sb.append('}');
        return sb.toString();
    }
    
    /**
     * Exception severity levels
     */
    public enum Severity {
        DEBUG,      // Informational, no action needed
        INFO,       // Informational, may require attention
        WARNING,    // Warning, operation completed but with issues
        ERROR,      // Error, operation failed but system is stable
        CRITICAL,   // Critical error, system stability affected
        FATAL       // Fatal error, system cannot continue
    }
}
