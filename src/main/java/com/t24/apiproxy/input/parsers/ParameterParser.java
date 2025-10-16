package com.t24.apiproxy.input.parsers;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.t24.apiproxy.exception.InputProcessingException;
import com.t24.apiproxy.model.ApiRequest;

/**
 * ParameterParser parses command-line style parameters into ApiRequest objects.
 * 
 * Supported formats:
 * 1. Simple URL: "https://api.example.com/endpoint"
 * 2. URL with method: "GET https://api.example.com/endpoint"
 * 3. Full format: "method=GET url=https://api.example.com/endpoint headers=Content-Type:application/json body={\"key\":\"value\"} timeout=5000"
 * 
 * Supported parameters:
 * - method: HTTP method (GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS)
 * - url: Target URL
 * - headers: Headers in format "Key1:Value1;Key2:Value2"
 * - body: Request body (JSON, XML, plain text)
 * - timeout: Request timeout in milliseconds
 * - auth: Authentication in format "type:username:password" (e.g., "BASIC:user:pass")
 * - cookies: Cookies in format "name1=value1;name2=value2"
 * - proxy: Proxy in format "host:port" or "host:port:username:password"
 * - follow: Follow redirects (true/false)
 * - verify: Verify SSL (true/false)
 * - retry: Retry count
 * - name: Request name/identifier
 */
public class ParameterParser implements Parser {
    
    // Regex patterns for parsing
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\w+)=([^\\s]+(?:\\s+(?!\\w+=)[^\\s]+)*)");
    private static final Pattern URL_ONLY_PATTERN = Pattern.compile("^https?://\\S+$");
    private static final Pattern METHOD_URL_PATTERN = Pattern.compile("^(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS)\\s+(https?://\\S+)$", Pattern.CASE_INSENSITIVE);
    
    /**
     * Default constructor for ParameterParser.
     */
    public ParameterParser() {
        // Initialize if needed
    }
    
    /**
     * Parses a parameter string into a list of ApiRequest objects.
     * Currently supports single request per parameter string.
     * 
     * @param arg The parameter string to parse
     * @return List containing the parsed ApiRequest
     * @throws Exception if parsing fails
     */
    @Override
    public List<ApiRequest> parse(String arg) throws Exception {
        if (arg == null || arg.trim().isEmpty()) {
            throw InputProcessingException.forParameter(
                "arg",
                arg,
                "Non-empty parameter string"
            );
        }
        
        arg = arg.trim();
        
        try {
            ApiRequest request;
            
            // Case 1: Simple URL only
            if (URL_ONLY_PATTERN.matcher(arg).matches()) {
                request = parseSimpleUrl(arg);
            }
            // Case 2: Method + URL
            else if (METHOD_URL_PATTERN.matcher(arg).matches()) {
                request = parseMethodUrl(arg);
            }
            // Case 3: Full key=value format
            else {
                request = parseKeyValueFormat(arg);
            }
            
            return Collections.singletonList(request);
            
        } catch (InputProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new InputProcessingException.Builder()
                .withInputType(InputProcessingException.InputType.PARAMETER)
                .withActualValue(arg)
                .withUserMessage("Failed to parse parameter string")
                .withTechnicalMessage("Failed to parse parameter string: " + e.getMessage())
                .withCause(e)
                .build();
        }
    }
    
    /**
     * Parses a simple URL string (default GET method).
     */
    private ApiRequest parseSimpleUrl(String urlString) throws Exception {
        return ApiRequest.newBuilder()
            .url(URI.create(urlString).toURL())
            .method("GET")
            .timeout(30000) // Default 30 seconds
            .build();
    }
    
    /**
     * Parses "METHOD URL" format.
     */
    private ApiRequest parseMethodUrl(String arg) throws Exception {
        Matcher matcher = METHOD_URL_PATTERN.matcher(arg);
        if (matcher.matches()) {
            String method = matcher.group(1).toUpperCase();
            String urlString = matcher.group(2);
            
            return ApiRequest.newBuilder()
                .method(method)
                .url(URI.create(urlString).toURL())
                .timeout(30000) // Default 30 seconds
                .build();
        }
        
        throw InputProcessingException.forParameter(
            "method_url",
            arg,
            "METHOD URL format (e.g., GET https://example.com)"
        );
    }
    
    /**
     * Parses full "key=value key=value" format.
     */
    private ApiRequest parseKeyValueFormat(String arg) throws Exception {
        Map<String, String> params = extractKeyValuePairs(arg);
        
        // URL is required
        if (!params.containsKey("url")) {
            throw InputProcessingException.forMissingParameter("url");
        }
        
        ApiRequest.Builder builder = ApiRequest.newBuilder();
        
        // Process each parameter
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey().toLowerCase();
            String value = entry.getValue();
            
            try {
                switch (key) {
                    case "url":
                        builder.url(URI.create(value).toURL());
                        break;
                        
                    case "method":
                        builder.method(value.toUpperCase());
                        break;
                        
                    case "headers":
                        parseHeaders(value, builder);
                        break;
                        
                    case "body":
                        builder.body(unquote(value));
                        break;
                        
                    case "timeout":
                        builder.timeout(Integer.parseInt(value));
                        break;
                        
                    case "auth":
                        parseAuth(value, builder);
                        break;
                        
                    case "cookies":
                        builder.cookies(unquote(value));
                        break;
                        
                    case "proxy":
                        parseProxy(value, builder);
                        break;
                        
                    case "follow":
                        builder.followRedirects(Boolean.parseBoolean(value));
                        break;
                        
                    case "verify":
                        builder.verifySsl(Boolean.parseBoolean(value));
                        break;
                        
                    case "retry":
                        builder.retryCount(Integer.parseInt(value));
                        break;
                        
                    case "name":
                        builder.name(value);
                        break;
                        
                    case "type":
                        builder.requestType(value.toUpperCase());
                        break;
                        
                    case "form":
                        builder.formData(value);
                        break;
                        
                    case "multipart":
                        builder.multipartData(value);
                        break;
                        
                    case "expected":
                        builder.expectedStatus(value);
                        break;
                        
                    case "validate":
                        builder.validateResponse(value);
                        break;
                        
                    case "output":
                        builder.outputFile(value);
                        break;
                        
                    case "retrydelay":
                        builder.retryDelay(Integer.parseInt(value));
                        break;
                        
                    default:
                        // Unknown parameter - could log warning or ignore
                        break;
                }
            } catch (NumberFormatException e) {
                throw InputProcessingException.forParameter(
                    key,
                    value,
                    "Valid number"
                );
            }
        }
        
        // Set default method if not provided
        if (params.get("method") == null) {
            builder.method("GET");
        }
        
        // Set default timeout if not provided
        if (params.get("timeout") == null) {
            builder.timeout(30000); // 30 seconds default
        }
        
        return builder.build();
    }
    
    /**
     * Extracts key=value pairs from the parameter string.
     */
    private Map<String, String> extractKeyValuePairs(String arg) {
        Map<String, String> params = new HashMap<>();
        Matcher matcher = KEY_VALUE_PATTERN.matcher(arg);
        
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2).trim();
            params.put(key, value);
        }
        
        return params;
    }
    
    /**
     * Parses headers in format "Key1:Value1;Key2:Value2".
     */
    private void parseHeaders(String headerString, ApiRequest.Builder builder) {
        if (headerString == null || headerString.isEmpty()) {
            return;
        }
        
        String unquoted = unquote(headerString);
        String[] headerPairs = unquoted.split(";");
        
        for (String pair : headerPairs) {
            String[] kv = pair.split(":", 2);
            if (kv.length == 2) {
                builder.addHeader(kv[0].trim(), kv[1].trim());
            }
        }
    }
    
    /**
     * Parses authentication in format "type:username:password".
     * Example: "BASIC:user:pass" or "BEARER:token"
     */
    private void parseAuth(String authString, ApiRequest.Builder builder) {
        if (authString == null || authString.isEmpty()) {
            return;
        }
        
        String unquoted = unquote(authString);
        String[] parts = unquoted.split(":", 3);
        
        if (parts.length >= 1) {
            builder.authType(parts[0].toUpperCase());
        }
        if (parts.length >= 2) {
            builder.authUsername(parts[1]);
        }
        if (parts.length >= 3) {
            builder.authPassword(parts[2]);
        }
    }
    
    /**
     * Parses proxy in format "host:port" or "host:port:username:password".
     */
    private void parseProxy(String proxyString, ApiRequest.Builder builder) {
        if (proxyString == null || proxyString.isEmpty()) {
            return;
        }
        
        String unquoted = unquote(proxyString);
        String[] parts = unquoted.split(":", 4);
        
        if (parts.length >= 1) {
            builder.proxyHost(parts[0]);
        }
        if (parts.length >= 2) {
            try {
                builder.proxyPort(Integer.parseInt(parts[1]));
            } catch (NumberFormatException e) {
                // Invalid port, skip
            }
        }
        if (parts.length >= 3) {
            builder.proxyUsername(parts[2]);
        }
        if (parts.length >= 4) {
            builder.proxyPassword(parts[3]);
        }
    }
    
    /**
     * Removes surrounding quotes from a string if present.
     */
    private String unquote(String str) {
        if (str == null || str.length() < 2) {
            return str;
        }
        
        if ((str.startsWith("\"") && str.endsWith("\"")) ||
            (str.startsWith("'") && str.endsWith("'"))) {
            return str.substring(1, str.length() - 1);
        }
        
        return str;
    }
}
