package com.t24.apiproxy.client.adapters;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;

import com.t24.apiproxy.client.RequestBuilder;
import com.t24.apiproxy.client.ResponseProcessor;
import com.t24.apiproxy.exception.NetworkException;
import com.t24.apiproxy.main.config.Configuration;
import com.t24.apiproxy.model.ApiRequest;
import com.t24.apiproxy.model.ApiResponse;

public class RestAdapter {
    private final RequestBuilder requestBuilder;
    private final ResponseProcessor responseProcessor;

    public RestAdapter(Configuration cfg) {
        this.requestBuilder = new RequestBuilder();
        this.responseProcessor = new ResponseProcessor();
    }

    /**
     * Executes a REST API call
     * @param req The API request to execute
     * @return ApiResponse containing the result
     * @throws Exception if the request fails
     */
    public ApiResponse call(ApiRequest req) throws Exception {
        if (req == null) {
            throw new IllegalArgumentException("ApiRequest cannot be null");
        }
        
        long startTime = System.currentTimeMillis();
        
        // Build the HTTP client with all configurations
        try (CloseableHttpClient httpClient = buildHttpClient(req)) {
            
            // Build the HTTP request
            HttpRequestBase httpRequest = requestBuilder.build(req);
            
            // Add authentication if specified
            addAuthentication(httpRequest, req);
            
            // Execute the request with retry logic
            ApiResponse response = executeWithRetry(httpClient, httpRequest, req, startTime);
            
            return response;
            
        } catch (IOException e) {
            throw new NetworkException("NETWORK_ERROR", 
                "Failed to execute REST request", 
                e.getMessage(), e);
        }
    }
    
    /**
     * Builds the HTTP client with SSL, proxy, and other configurations
     */
    private CloseableHttpClient buildHttpClient(ApiRequest req) throws Exception {
        HttpClientBuilder clientBuilder = HttpClients.custom();
        
        // SSL Configuration
        if (!req.isVerifySsl()) {
            SSLContextBuilder sslBuilder = new SSLContextBuilder();
            sslBuilder.loadTrustMaterial(null, new TrustAllStrategy());
            
            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(
                sslBuilder.build(), 
                NoopHostnameVerifier.INSTANCE
            );
            
            clientBuilder.setSSLSocketFactory(sslSocketFactory);
        }
        
        // Proxy Configuration
        if (req.getProxyHost() != null && !req.getProxyHost().isEmpty()) {
            HttpHost proxy = new HttpHost(req.getProxyHost(), req.getProxyPort());
            clientBuilder.setProxy(proxy);
            
            // Proxy Authentication
            if (req.getProxyUsername() != null && !req.getProxyUsername().isEmpty()) {
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(
                    new AuthScope(req.getProxyHost(), req.getProxyPort()),
                    new UsernamePasswordCredentials(req.getProxyUsername(), req.getProxyPassword())
                );
                clientBuilder.setDefaultCredentialsProvider(credsProvider);
            }
        }
        
        return clientBuilder.build();
    }
    
    /**
     * Adds authentication headers to the request
     */
    private void addAuthentication(HttpRequestBase httpRequest, ApiRequest req) {
        String authType = req.getAuthType();
        if (authType == null || authType.isEmpty()) {
            return;
        }
        
        switch (authType.toUpperCase()) {
            case "BASIC":
                if (req.getAuthUsername() != null && req.getAuthPassword() != null) {
                    String credentials = req.getAuthUsername() + ":" + req.getAuthPassword();
                    String encodedCredentials = java.util.Base64.getEncoder()
                        .encodeToString(credentials.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    httpRequest.setHeader("Authorization", "Basic " + encodedCredentials);
                }
                break;
                
            case "BEARER":
                if (req.getAuthPassword() != null) { // Token stored in password field
                    httpRequest.setHeader("Authorization", "Bearer " + req.getAuthPassword());
                }
                break;
                
            case "API_KEY":
                if (req.getAuthPassword() != null) { // API key stored in password field
                    httpRequest.setHeader("X-API-Key", req.getAuthPassword());
                }
                break;
                
            case "DIGEST":
            case "NTLM":
                // These require CredentialsProvider, handled in buildHttpClient if needed
                // For now, log a warning
                System.err.println("WARNING: " + authType + " authentication not fully implemented yet");
                break;
                
            default:
                System.err.println("WARNING: Unknown authentication type: " + authType);
        }
    }
    
    /**
     * Executes the request with retry logic
     */
    private ApiResponse executeWithRetry(CloseableHttpClient httpClient, HttpRequestBase httpRequest, 
                                         ApiRequest req, long startTime) throws Exception {
        int retryCount = req.getRetryCount() > 0 ? req.getRetryCount() : 0;
        int retryDelay = req.getRetryDelay() > 0 ? req.getRetryDelay() : 1000;
        
        Exception lastException = null;
        
        for (int attempt = 0; attempt <= retryCount; attempt++) {
            try {
                // Execute the request
                try (CloseableHttpResponse httpResponse = httpClient.execute(httpRequest)) {
                    
                    // Process the response
                    ApiResponse response = responseProcessor.process(httpResponse);
                    
                    // Calculate response time
                    long responseTime = System.currentTimeMillis() - startTime;
                    response = ApiResponse.newBuilder()
                        .statusCode(response.getStatusCode())
                        .statusMessage(response.getStatusMessage())
                        .headers(response.getHeaders())
                        .cookies(response.getCookies())
                        .body(response.getBody())
                        .contentType(response.getContentType())
                        .contentLength(response.getContentLength())
                        .responseTime(responseTime)
                        .receivedAt(response.getReceivedAt())
                        .errorMessage(response.getErrorMessage())
                        .success(response.isSuccess())
                        .build();
                    
                    // If successful or not a retryable error, return
                    if (response.isSuccess() || !isRetryableStatusCode(response.getStatusCode())) {
                        return response;
                    }
                    
                    lastException = new NetworkException("HTTP_ERROR", 
                        "HTTP " + response.getStatusCode() + ": " + response.getStatusMessage(),
                        "Server returned error status code");
                }
                
            } catch (IOException e) {
                lastException = e;
            }
            
            // If not the last attempt, wait before retrying
            if (attempt < retryCount) {
                System.err.println("Request failed, retrying in " + retryDelay + "ms... (attempt " + 
                    (attempt + 1) + "/" + (retryCount + 1) + ")");
                Thread.sleep(retryDelay);
            }
        }
        
        // All retries failed
        String errorMsg = "Request failed after " + (retryCount + 1) + " attempts";
        String techMsg = lastException != null ? lastException.getMessage() : "Unknown error";
        throw new NetworkException("NETWORK_ERROR", errorMsg, techMsg, lastException);
    }
    
    /**
     * Determines if a status code is retryable
     */
    private boolean isRetryableStatusCode(int statusCode) {
        // Retry on server errors (5xx) and some client errors
        return statusCode >= 500 || statusCode == 408 || statusCode == 429;
    }
}
