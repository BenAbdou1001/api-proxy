package com.t24.apiproxy.input.validation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.t24.apiproxy.exception.ValidationException;
import com.t24.apiproxy.model.ApiRequest;

public class InputValidator {
    
    // Valid HTTP methods
    private static final Set<String> VALID_HTTP_METHODS = new HashSet<>(Arrays.asList(
        "GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS", "TRACE", "CONNECT"
    ));
    
    // Valid authentication types
    private static final Set<String> VALID_AUTH_TYPES = new HashSet<>(Arrays.asList(
        "BASIC", "BEARER", "API_KEY", "OAUTH2", "DIGEST", "NTLM"
    ));
    
    // Valid request types
    private static final Set<String> VALID_REQUEST_TYPES = new HashSet<>(Arrays.asList(
        "REST", "GRAPHQL", "SOAP", "GRPC"
    ));
    
    // Valid URL protocols
    private static final Set<String> VALID_PROTOCOLS = new HashSet<>(Arrays.asList(
        "http", "https"
    ));
    
    /**
     * Validates a list of API requests
     * @param reqs List of API requests to validate
     * @throws ValidationException if any request is invalid
     */
    public static void validate(List<ApiRequest> reqs) {
        if (reqs == null || reqs.isEmpty()) {
            throw new ValidationException("Request list cannot be null or empty", "Request list cannot be null or empty");
        }
        
        int index = 0;
        for (ApiRequest req : reqs) {
            validateRequest(req, index);
            index++;
        }
    }
    
    /**
     * Validates a single API request
     * @param req The request to validate
     * @param index The index of the request in the list (for error messages)
     * @throws ValidationException if the request is invalid
     */
    private static void validateRequest(ApiRequest req, int index) {
        String prefix = "Request #" + (index + 1) + ": ";
        
        // Validate URL (mandatory)
        validateUrl(req, prefix);
        
        // Validate HTTP method (mandatory)
        validateMethod(req, prefix);
        
        // Validate request type (if present)
        validateRequestType(req, prefix);
        
        // Validate authentication settings
        validateAuthentication(req, prefix);
        
        // Validate timeout values
        validateTimeouts(req, prefix);
        
        // Validate redirect settings
        validateRedirects(req, prefix);
        
        // Validate proxy settings
        validateProxy(req, prefix);
        
        // Validate retry settings
        validateRetry(req, prefix);
        
        // Validate headers
        validateHeaders(req, prefix);
        
        // Validate body and content-type consistency
        validateBodyConsistency(req, prefix);
        
        // Validate expected status format
        validateExpectedStatus(req, prefix);
    }
    
    /**
     * Validates the URL field
     */
    private static void validateUrl(ApiRequest req, String prefix) {
        if (req.getUrl() == null) {
            throw new ValidationException(prefix + "URL is required");
        }
        
        String protocol = req.getUrl().getProtocol();
        if (!VALID_PROTOCOLS.contains(protocol.toLowerCase())) {
            throw new ValidationException(prefix + "Invalid URL protocol '" + protocol + 
                "'. Only HTTP and HTTPS are supported");
        }
        
        String host = req.getUrl().getHost();
        if (host == null || host.trim().isEmpty()) {
            throw new ValidationException(prefix + "URL host cannot be empty");
        }
    }
    
    /**
     * Validates the HTTP method
     */
    private static void validateMethod(ApiRequest req, String prefix) {
        if (req.getMethod() == null || req.getMethod().trim().isEmpty()) {
            throw new ValidationException(prefix + "HTTP method is required");
        }
        
        String method = req.getMethod().toUpperCase();
        if (!VALID_HTTP_METHODS.contains(method)) {
            throw new ValidationException(prefix + "Invalid HTTP method '" + req.getMethod() + 
                "'. Valid methods are: " + VALID_HTTP_METHODS);
        }
        
        // Validate that GET and HEAD requests don't have a body
        if (("GET".equals(method) || "HEAD".equals(method)) && req.getBody() != null) {
            throw new ValidationException(prefix + method + " requests cannot have a request body");
        }
    }
    
    /**
     * Validates the request type
     */
    private static void validateRequestType(ApiRequest req, String prefix) {
        if (req.getRequestType() != null && !req.getRequestType().trim().isEmpty()) {
            String type = req.getRequestType().toUpperCase();
            if (!VALID_REQUEST_TYPES.contains(type)) {
                throw new ValidationException(prefix + "Invalid request type '" + req.getRequestType() + 
                    "'. Valid types are: " + VALID_REQUEST_TYPES);
            }
        }
    }
    
    /**
     * Validates authentication settings
     */
    private static void validateAuthentication(ApiRequest req, String prefix) {
        String authType = req.getAuthType();
        
        if (authType != null && !authType.trim().isEmpty()) {
            if (!VALID_AUTH_TYPES.contains(authType.toUpperCase())) {
                throw new ValidationException(prefix + "Invalid authentication type '" + authType + 
                    "'. Valid types are: " + VALID_AUTH_TYPES);
            }
            
            // For BASIC, DIGEST, NTLM auth, username and password are required
            if (authType.equalsIgnoreCase("BASIC") || 
                authType.equalsIgnoreCase("DIGEST") || 
                authType.equalsIgnoreCase("NTLM")) {
                
                if (req.getAuthUsername() == null || req.getAuthUsername().trim().isEmpty()) {
                    throw new ValidationException(prefix + authType + " authentication requires a username");
                }
                
                if (req.getAuthPassword() == null || req.getAuthPassword().trim().isEmpty()) {
                    throw new ValidationException(prefix + authType + " authentication requires a password");
                }
            }
        }
    }
    
    /**
     * Validates timeout values
     */
    private static void validateTimeouts(ApiRequest req, String prefix) {
        if (req.getTimeout() < 0) {
            throw new ValidationException(prefix + "Timeout cannot be negative");
        }
        
        if (req.getConnectionTimeout() < 0) {
            throw new ValidationException(prefix + "Connection timeout cannot be negative");
        }
        
        if (req.getReadTimeout() < 0) {
            throw new ValidationException(prefix + "Read timeout cannot be negative");
        }
        
        // Warning: very long timeouts (more than 5 minutes)
        if (req.getTimeout() > 300000) {
            System.err.println("WARNING: " + prefix + "Timeout is set to more than 5 minutes (" + 
                req.getTimeout() + "ms)");
        }
    }
    
    /**
     * Validates redirect settings
     */
    private static void validateRedirects(ApiRequest req, String prefix) {
        if (req.getMaxRedirects() < 0) {
            throw new ValidationException(prefix + "Max redirects cannot be negative");
        }
        
        if (req.getMaxRedirects() > 50) {
            throw new ValidationException(prefix + "Max redirects cannot exceed 50 (got " + 
                req.getMaxRedirects() + ")");
        }
    }
    
    /**
     * Validates proxy settings
     */
    private static void validateProxy(ApiRequest req, String prefix) {
        String proxyHost = req.getProxyHost();
        int proxyPort = req.getProxyPort();
        
        // If proxy host is set, validate port
        if (proxyHost != null && !proxyHost.trim().isEmpty()) {
            if (proxyPort <= 0 || proxyPort > 65535) {
                throw new ValidationException(prefix + "Invalid proxy port " + proxyPort + 
                    ". Port must be between 1 and 65535");
            }
        }
        
        // If proxy authentication is set, ensure proxy host is also set
        if ((req.getProxyUsername() != null || req.getProxyPassword() != null)) {
            if (proxyHost == null || proxyHost.trim().isEmpty()) {
                throw new ValidationException(prefix + "Proxy credentials provided but proxy host is missing");
            }
        }
    }
    
    /**
     * Validates retry settings
     */
    private static void validateRetry(ApiRequest req, String prefix) {
        if (req.getRetryCount() < 0) {
            throw new ValidationException(prefix + "Retry count cannot be negative");
        }
        
        if (req.getRetryCount() > 10) {
            throw new ValidationException(prefix + "Retry count cannot exceed 10 (got " + 
                req.getRetryCount() + ")");
        }
        
        if (req.getRetryDelay() < 0) {
            throw new ValidationException(prefix + "Retry delay cannot be negative");
        }
    }
    
    /**
     * Validates headers
     */
    private static void validateHeaders(ApiRequest req, String prefix) {
        if (req.getHeaders() != null) {
            for (String headerName : req.getHeaders().keySet()) {
                if (headerName == null || headerName.trim().isEmpty()) {
                    throw new ValidationException(prefix + "Header name cannot be null or empty");
                }
                
                // Check for invalid characters in header names (basic validation)
                if (headerName.contains("\n") || headerName.contains("\r")) {
                    throw new ValidationException(prefix + "Header name '" + headerName + 
                        "' contains invalid characters");
                }
            }
        }
    }
    
    /**
     * Validates body and content-type consistency
     */
    private static void validateBodyConsistency(ApiRequest req, String prefix) {
        // Check that only one body type is set
        int bodyTypeCount = 0;
        if (req.getBody() != null) bodyTypeCount++;
        if (req.getFormData() != null) bodyTypeCount++;
        if (req.getMultipartData() != null) bodyTypeCount++;
        
        if (bodyTypeCount > 1) {
            throw new ValidationException(prefix + 
                "Only one of body, formData, or multipartData should be set");
        }
        
        // Validate Content-Type for specific body types
        if (req.getHeaders() != null) {
            String contentType = req.getHeaders().get("Content-Type");
            
            if (req.getFormData() != null && contentType != null) {
                if (!contentType.contains("application/x-www-form-urlencoded")) {
                    System.err.println("WARNING: " + prefix + 
                        "formData is set but Content-Type is not 'application/x-www-form-urlencoded'");
                }
            }
            
            if (req.getMultipartData() != null && contentType != null) {
                if (!contentType.contains("multipart/form-data")) {
                    System.err.println("WARNING: " + prefix + 
                        "multipartData is set but Content-Type is not 'multipart/form-data'");
                }
            }
        }
    }
    
    /**
     * Validates expected status format
     */
    private static void validateExpectedStatus(ApiRequest req, String prefix) {
        String expectedStatus = req.getExpectedStatus();
        if (expectedStatus != null && !expectedStatus.trim().isEmpty()) {
            // Expected status can be: "200", "200,201", "2xx", "200-299"
            String[] parts = expectedStatus.split(",");
            for (String part : parts) {
                part = part.trim();
                
                // Check for range format (e.g., "200-299")
                if (part.contains("-")) {
                    String[] range = part.split("-");
                    if (range.length != 2) {
                        throw new ValidationException(prefix + "Invalid expected status range format: " + part);
                    }
                    try {
                        int start = Integer.parseInt(range[0].trim());
                        int end = Integer.parseInt(range[1].trim());
                        if (start < 100 || start > 599 || end < 100 || end > 599 || start > end) {
                            throw new ValidationException(prefix + "Invalid status code range: " + part);
                        }
                    } catch (NumberFormatException e) {
                        throw new ValidationException(prefix + "Invalid expected status range: " + part);
                    }
                }
                // Check for wildcard format (e.g., "2xx")
                else if (part.endsWith("xx")) {
                    String prefix_digit = part.substring(0, part.length() - 2);
                    try {
                        int digit = Integer.parseInt(prefix_digit);
                        if (digit < 1 || digit > 5) {
                            throw new ValidationException(prefix + "Invalid status code wildcard: " + part);
                        }
                    } catch (NumberFormatException e) {
                        throw new ValidationException(prefix + "Invalid expected status wildcard: " + part);
                    }
                }
                // Check for specific status code
                else {
                    try {
                        int status = Integer.parseInt(part);
                        if (status < 100 || status > 599) {
                            throw new ValidationException(prefix + "Invalid status code: " + status + 
                                ". Must be between 100 and 599");
                        }
                    } catch (NumberFormatException e) {
                        throw new ValidationException(prefix + "Invalid expected status format: " + part);
                    }
                }
            }
        }
    }
}

