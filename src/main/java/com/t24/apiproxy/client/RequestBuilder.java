package com.t24.apiproxy.client;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import com.t24.apiproxy.model.ApiRequest;

public class RequestBuilder {
    
    /**
     * Builds an Apache HttpClient request from an ApiRequest
     * @param req The ApiRequest to convert
     * @return HttpRequestBase ready to execute
     */
    public HttpRequestBase build(ApiRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("ApiRequest cannot be null");
        }
        if (req.getMethod() == null || req.getUrl() == null) {
            throw new IllegalArgumentException("ApiRequest method and URL must not be null");
        }
        
        // Build URL with query parameters
        String url = buildUrlWithQueryParams(req);
        
        // Create the appropriate HTTP method
        HttpRequestBase httpRequest = createHttpMethod(req.getMethod(), url);
        
        // Set headers
        setHeaders(httpRequest, req);
        
        // Set cookies
        setCookies(httpRequest, req);
        
        // Set body (for POST, PUT, PATCH)
        setBody(httpRequest, req);
        
        // Set timeouts and connection config
        setRequestConfig(httpRequest, req);
        
        return httpRequest;
    }
    
    /**
     * Builds URL with query parameters
     */
    private String buildUrlWithQueryParams(ApiRequest req) {
        String url = req.getUrl().toString();
        
        if (req.getQueryParams() != null && !req.getQueryParams().isEmpty()) {
            StringBuilder sb = new StringBuilder(url);
            boolean hasQuery = url.contains("?");
            
            for (Map.Entry<String, String> entry : req.getQueryParams().entrySet()) {
                if (hasQuery) {
                    sb.append("&");
                } else {
                    sb.append("?");
                    hasQuery = true;
                }
                sb.append(urlEncode(entry.getKey()))
                  .append("=")
                  .append(urlEncode(entry.getValue()));
            }
            return sb.toString();
        }
        
        return url;
    }
    
    /**
     * Simple URL encoding
     */
    private String urlEncode(String value) {
        try {
            return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (java.io.UnsupportedEncodingException e) {
            return value; // Shouldn't happen with UTF-8
        }
    }
    
    /**
     * Creates the appropriate HTTP method object
     */
    private HttpRequestBase createHttpMethod(String method, String url) {
        switch (method.toUpperCase()) {
            case "GET":
                return new HttpGet(url);
            case "POST":
                return new HttpPost(url);
            case "PUT":
                return new HttpPut(url);
            case "DELETE":
                return new HttpDelete(url);
            case "PATCH":
                return new HttpPatch(url);
            case "HEAD":
                return new HttpHead(url);
            case "OPTIONS":
                return new HttpOptions(url);
            case "TRACE":
                return new HttpTrace(url);
            default:
                throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        }
    }
    
    /**
     * Sets headers on the HTTP request
     */
    private void setHeaders(HttpRequestBase httpRequest, ApiRequest req) {
        if (req.getHeaders() != null && !req.getHeaders().isEmpty()) {
            for (Map.Entry<String, String> header : req.getHeaders().entrySet()) {
                httpRequest.setHeader(header.getKey(), header.getValue());
            }
        }
    }
    
    /**
     * Sets cookies on the HTTP request
     */
    private void setCookies(HttpRequestBase httpRequest, ApiRequest req) {
        if (req.getCookies() != null && !req.getCookies().isEmpty()) {
            httpRequest.setHeader("Cookie", req.getCookies());
        }
    }
    
    /**
     * Sets the request body for methods that support it
     */
    private void setBody(HttpRequestBase httpRequest, ApiRequest req) {
        if (!(httpRequest instanceof HttpEntityEnclosingRequestBase)) {
            return; // Method doesn't support body (GET, HEAD, etc.)
        }
        
        HttpEntityEnclosingRequestBase entityRequest = (HttpEntityEnclosingRequestBase) httpRequest;
        HttpEntity entity = null;
        
        // Priority: body > formData > multipartData
        if (req.getBody() != null) {
            // Regular body (JSON, XML, plain text, etc.)
            String bodyContent = req.getBody().toString();
            ContentType contentType = determineContentType(req);
            entity = new StringEntity(bodyContent, contentType);
            
        } else if (req.getFormData() != null && !req.getFormData().isEmpty()) {
            // Form data: application/x-www-form-urlencoded
            entity = new StringEntity(
                req.getFormData(), 
                ContentType.APPLICATION_FORM_URLENCODED
            );
            
        } else if (req.getMultipartData() != null && !req.getMultipartData().isEmpty()) {
            // Multipart form data (simplified - would need MultipartEntityBuilder for real files)
            entity = new StringEntity(
                req.getMultipartData(), 
                ContentType.MULTIPART_FORM_DATA
            );
        }
        
        if (entity != null) {
            entityRequest.setEntity(entity);
        }
    }
    
    /**
     * Determines the content type from headers or defaults to application/json
     */
    private ContentType determineContentType(ApiRequest req) {
        if (req.getHeaders() != null && req.getHeaders().containsKey("Content-Type")) {
            String contentTypeStr = req.getHeaders().get("Content-Type");
            try {
                return ContentType.parse(contentTypeStr);
            } catch (Exception e) {
                // Fall back to default
            }
        }
        // Default to JSON
        return ContentType.APPLICATION_JSON;
    }
    
    /**
     * Sets timeout and connection configuration
     */
    private void setRequestConfig(HttpRequestBase httpRequest, ApiRequest req) {
        RequestConfig.Builder configBuilder = RequestConfig.custom();
        
        // Connection timeout
        if (req.getConnectionTimeout() > 0) {
            configBuilder.setConnectTimeout(req.getConnectionTimeout());
        }
        
        // Socket/Read timeout
        if (req.getReadTimeout() > 0) {
            configBuilder.setSocketTimeout(req.getReadTimeout());
        }
        
        // General timeout (if specified and others aren't)
        if (req.getTimeout() > 0) {
            if (req.getConnectionTimeout() <= 0) {
                configBuilder.setConnectTimeout(req.getTimeout());
            }
            if (req.getReadTimeout() <= 0) {
                configBuilder.setSocketTimeout(req.getTimeout());
            }
        }
        
        // Follow redirects
        if (req.isFollowRedirects()) {
            configBuilder.setRedirectsEnabled(true);
            if (req.getMaxRedirects() > 0) {
                configBuilder.setMaxRedirects(req.getMaxRedirects());
            }
        } else {
            configBuilder.setRedirectsEnabled(false);
        }
        
        httpRequest.setConfig(configBuilder.build());
    }
}
