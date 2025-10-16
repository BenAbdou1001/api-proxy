package com.t24.apiproxy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.t24.apiproxy.model.ApiRequest;
import com.t24.apiproxy.model.ApiResponse;

/**
 * Utility class for logging operations
 */
public class LoggingUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingUtil.class);
    
    // MDC keys for contextual logging
    public static final String MDC_REQUEST_ID = "requestId";
    public static final String MDC_URL = "url";
    public static final String MDC_METHOD = "method";
    public static final String MDC_STATUS_CODE = "statusCode";
    public static final String MDC_RESPONSE_TIME = "responseTime";
    public static final String MDC_USER = "user";
    public static final String MDC_SESSION = "session";
    
    /**
     * Initializes the logging system
     */
    public static void init() {
        Logger initLogger = LoggerFactory.getLogger(LoggingUtil.class);
        initLogger.info("LoggingUtil initialized successfully");
    }
    
    /**
     * Gets a logger for a specific class
     * 
     * @param cls Class to get logger for
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> cls) {
        return LoggerFactory.getLogger(cls);
    }
    
    /**
     * Gets a logger with a specific name
     * 
     * @param name Logger name
     * @return Logger instance
     */
    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
    
    /**
     * Logs an API request
     * 
     * @param req API request to log
     */
    public static void logRequest(ApiRequest req) {
        if (req == null) return;
        
        Logger requestLogger = LoggerFactory.getLogger("api.request");
        
        // Set MDC context
        if (req.getUrl() != null) {
            MDC.put(MDC_URL, req.getUrl().toString());
        }
        if (req.getMethod() != null) {
            MDC.put(MDC_METHOD, req.getMethod());
        }
        
        requestLogger.info("API Request: {} {}", req.getMethod(), req.getUrl());
        
        if (req.getHeaders() != null && !req.getHeaders().isEmpty()) {
            requestLogger.debug("Request Headers: {}", req.getHeaders());
        }
        
        if (req.getBody() != null) {
            requestLogger.debug("Request Body: {}", truncateForLog(req.getBody().toString(), 500));
        }
    }
    
    /**
     * Logs an API response
     * 
     * @param res API response to log
     */
    public static void logResponse(ApiResponse res) {
        if (res == null) return;
        
        Logger responseLogger = LoggerFactory.getLogger("api.response");
        
        // Set MDC context
        MDC.put(MDC_STATUS_CODE, String.valueOf(res.getStatusCode()));
        if (res.getResponseTime() > 0) {
            MDC.put(MDC_RESPONSE_TIME, String.valueOf(res.getResponseTime()));
        }
        
        String status = res.isSuccess() ? "SUCCESS" : "FAILED";
        responseLogger.info("API Response: {} - Status: {} in {}ms", 
            status, res.getStatusCode(), res.getResponseTime());
        
        if (res.getHeaders() != null && !res.getHeaders().isEmpty()) {
            responseLogger.debug("Response Headers: {}", res.getHeaders());
        }
        
        if (res.getBody() != null) {
            responseLogger.debug("Response Body: {}", truncateForLog(res.getBody().toString(), 500));
        }
        
        if (res.getErrorMessage() != null) {
            responseLogger.error("Error: {}", res.getErrorMessage());
        }
    }
    
    /**
     * Logs an API request-response pair
     * 
     * @param req API request
     * @param res API response
     */
    public static void logRequestResponse(ApiRequest req, ApiResponse res) {
        logRequest(req);
        logResponse(res);
    }
    
    /**
     * Logs an exception with context
     * 
     * @param logger Logger to use
     * @param message Error message
     * @param throwable Exception to log
     */
    public static void logException(Logger logger, String message, Throwable throwable) {
        if (logger == null) {
            logger = LoggingUtil.logger;
        }
        logger.error(message, throwable);
    }
    
    /**
     * Logs an exception with API request context
     * 
     * @param req API request that caused the exception
     * @param throwable Exception to log
     */
    public static void logException(ApiRequest req, Throwable throwable) {
        Logger exceptionLogger = LoggerFactory.getLogger("api.exception");
        
        if (req != null) {
            exceptionLogger.error("Exception during API call: {} {}", 
                req.getMethod(), req.getUrl(), throwable);
        } else {
            exceptionLogger.error("Exception during API call", throwable);
        }
    }
    
    /**
     * Sets MDC context with request ID
     * 
     * @param requestId Request ID
     */
    public static void setRequestId(String requestId) {
        if (requestId != null && !requestId.isEmpty()) {
            MDC.put(MDC_REQUEST_ID, requestId);
        }
    }
    
    /**
     * Sets MDC context with user information
     * 
     * @param user User identifier
     */
    public static void setUser(String user) {
        if (user != null && !user.isEmpty()) {
            MDC.put(MDC_USER, user);
        }
    }
    
    /**
     * Sets MDC context with session information
     * 
     * @param session Session identifier
     */
    public static void setSession(String session) {
        if (session != null && !session.isEmpty()) {
            MDC.put(MDC_SESSION, session);
        }
    }
    
    /**
     * Clears MDC context
     */
    public static void clearMDC() {
        MDC.clear();
    }
    
    /**
     * Clears a specific MDC key
     * 
     * @param key MDC key to clear
     */
    public static void clearMDC(String key) {
        if (key != null) {
            MDC.remove(key);
        }
    }
    
    /**
     * Logs performance metrics
     * 
     * @param operation Operation name
     * @param durationMillis Duration in milliseconds
     */
    public static void logPerformance(String operation, long durationMillis) {
        Logger perfLogger = LoggerFactory.getLogger("api.performance");
        
        if (durationMillis > 5000) {
            perfLogger.warn("SLOW: {} took {}ms", operation, durationMillis);
        } else if (durationMillis > 1000) {
            perfLogger.info("Operation {} took {}ms", operation, durationMillis);
        } else {
            perfLogger.debug("Operation {} took {}ms", operation, durationMillis);
        }
    }
    
    /**
     * Logs a debug message with formatted arguments
     * 
     * @param logger Logger to use
     * @param format Message format
     * @param args Arguments
     */
    public static void debug(Logger logger, String format, Object... args) {
        if (logger != null && logger.isDebugEnabled()) {
            logger.debug(format, args);
        }
    }
    
    /**
     * Logs an info message with formatted arguments
     * 
     * @param logger Logger to use
     * @param format Message format
     * @param args Arguments
     */
    public static void info(Logger logger, String format, Object... args) {
        if (logger != null && logger.isInfoEnabled()) {
            logger.info(format, args);
        }
    }
    
    /**
     * Logs a warning message with formatted arguments
     * 
     * @param logger Logger to use
     * @param format Message format
     * @param args Arguments
     */
    public static void warn(Logger logger, String format, Object... args) {
        if (logger != null && logger.isWarnEnabled()) {
            logger.warn(format, args);
        }
    }
    
    /**
     * Logs an error message with formatted arguments
     * 
     * @param logger Logger to use
     * @param format Message format
     * @param args Arguments
     */
    public static void error(Logger logger, String format, Object... args) {
        if (logger != null && logger.isErrorEnabled()) {
            logger.error(format, args);
        }
    }
    
    /**
     * Creates a formatted log entry for an HTTP transaction
     * 
     * @param method HTTP method
     * @param url URL
     * @param statusCode Status code
     * @param responseTime Response time in milliseconds
     * @return Formatted log entry
     */
    public static String formatHttpLog(String method, String url, int statusCode, long responseTime) {
        return String.format("%s %s -> %d (%dms)", method, url, statusCode, responseTime);
    }
    
    /**
     * Truncates a string for logging purposes
     * 
     * @param text Text to truncate
     * @param maxLength Maximum length
     * @return Truncated text
     */
    private static String truncateForLog(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + "... [truncated]";
    }
    
    /**
     * Masks sensitive information in a string
     * 
     * @param text Text that may contain sensitive data
     * @return Masked text
     */
    public static String maskSensitive(String text) {
        if (text == null) return null;
        
        // Mask common sensitive patterns
        String masked = text;
        
        // Mask passwords
        masked = masked.replaceAll("(?i)(password|pwd|secret)\\s*[:=]\\s*[^\\s,;]+", "$1=***");
        
        // Mask API keys
        masked = masked.replaceAll("(?i)(api[_-]?key|token)\\s*[:=]\\s*[^\\s,;]+", "$1=***");
        
        // Mask authorization headers
        masked = masked.replaceAll("(?i)(authorization|auth)\\s*:\\s*[^\\s,;]+", "$1: ***");
        
        // Mask credit card numbers (simple pattern)
        masked = masked.replaceAll("\\b\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}[\\s-]?\\d{4}\\b", "****-****-****-****");
        
        return masked;
    }
    
    /**
     * Checks if debug logging is enabled for a logger
     * 
     * @param logger Logger to check
     * @return true if debug is enabled
     */
    public static boolean isDebugEnabled(Logger logger) {
        return logger != null && logger.isDebugEnabled();
    }
    
    /**
     * Checks if trace logging is enabled for a logger
     * 
     * @param logger Logger to check
     * @return true if trace is enabled
     */
    public static boolean isTraceEnabled(Logger logger) {
        return logger != null && logger.isTraceEnabled();
    }
}
