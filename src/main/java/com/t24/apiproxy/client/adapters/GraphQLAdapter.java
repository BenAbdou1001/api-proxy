package com.t24.apiproxy.client.adapters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.t24.apiproxy.exception.NetworkException;
import com.t24.apiproxy.main.config.Configuration;
import com.t24.apiproxy.model.ApiRequest;
import com.t24.apiproxy.model.ApiResponse;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GraphQLAdapter {
    private final OkHttpClient client;
    private final String endpoint;

    public GraphQLAdapter(Configuration cfg) {
        this.client = new OkHttpClient();
        // Try to get endpoint from configuration, fall back to URL from request
        this.endpoint = cfg.getRequiredOrDefault("graphql.endpoint", null);
    }

    /**
     * Executes a GraphQL API call
     * @param req The API request to execute
     * @return ApiResponse containing the result
     * @throws Exception if the request fails
     */
    public ApiResponse call(ApiRequest req) throws Exception {
        if (req == null) {
            throw new IllegalArgumentException("ApiRequest cannot be null");
        }
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Determine the endpoint URL
            String url = endpoint != null ? endpoint : req.getUrl().toString();
            
            // Build GraphQL request body
            JSONObject json = buildGraphQLRequest(req);
            
            RequestBody body = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json; charset=utf-8")
            );

            // Build request with headers
            Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .post(body);
            
            // Add custom headers
            if (req.getHeaders() != null && !req.getHeaders().isEmpty()) {
                for (Map.Entry<String, String> header : req.getHeaders().entrySet()) {
                    requestBuilder.addHeader(header.getKey(), header.getValue());
                }
            }
            
            // Add authentication if specified
            addAuthentication(requestBuilder, req);
            
            Request request = requestBuilder.build();
            
            // Execute request
            try (Response res = client.newCall(request).execute()) {
                
                // Extract headers
                Map<String, String> responseHeaders = new HashMap<>();
                Headers okHeaders = res.headers();
                for (int i = 0; i < okHeaders.size(); i++) {
                    responseHeaders.put(okHeaders.name(i), okHeaders.value(i));
                }
                
                // Extract body
                String responseBody = res.body() != null ? res.body().string() : "";
                
                // Calculate response time
                long responseTime = System.currentTimeMillis() - startTime;
                
                return ApiResponse.newBuilder()
                    .statusCode(res.code())
                    .statusMessage(res.message())
                    .headers(responseHeaders)
                    .body(responseBody)
                    .contentType(res.body() != null && res.body().contentType() != null ? 
                        res.body().contentType().toString() : null)
                    .contentLength(res.body() != null ? res.body().contentLength() : 0)
                    .responseTime(responseTime)
                    .receivedAt(System.currentTimeMillis())
                    .success(res.isSuccessful())
                    .build();
            }
            
        } catch (IOException e) {
            throw new NetworkException("NETWORK_ERROR", 
                "Failed to execute GraphQL request", 
                e.getMessage(), e);
        }
    }
    
    /**
     * Builds the GraphQL request JSON body
     */
    private JSONObject buildGraphQLRequest(ApiRequest req) {
        JSONObject json = new JSONObject();
        
        // Get query from body
        if (req.getBody() != null) {
            String bodyStr = req.getBody().toString();
            
            // If body is already JSON, parse it
            if (bodyStr.trim().startsWith("{")) {
                try {
                    return new JSONObject(bodyStr);
                } catch (Exception e) {
                    // Not valid JSON, treat as query string
                }
            }
            
            // Otherwise, treat body as the query string
            json.put("query", bodyStr);
            
        } else {
            throw new IllegalArgumentException("GraphQL query cannot be null. Provide the query in the request body.");
        }
        
        // Add variables if present in query params
        if (req.getQueryParams() != null && !req.getQueryParams().isEmpty()) {
            JSONObject variables = new JSONObject(req.getQueryParams());
            json.put("variables", variables);
        }
        
        return json;
    }
    
    /**
     * Adds authentication to the request
     */
    private void addAuthentication(Request.Builder requestBuilder, ApiRequest req) {
        String authType = req.getAuthType();
        if (authType == null || authType.isEmpty()) {
            return;
        }
        
        switch (authType.toUpperCase()) {
            case "BEARER":
                if (req.getAuthPassword() != null) { // Token stored in password field
                    requestBuilder.addHeader("Authorization", "Bearer " + req.getAuthPassword());
                }
                break;
                
            case "BASIC":
                if (req.getAuthUsername() != null && req.getAuthPassword() != null) {
                    String credentials = req.getAuthUsername() + ":" + req.getAuthPassword();
                    String encodedCredentials = java.util.Base64.getEncoder()
                        .encodeToString(credentials.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                    requestBuilder.addHeader("Authorization", "Basic " + encodedCredentials);
                }
                break;
                
            case "API_KEY":
                if (req.getAuthPassword() != null) { // API key stored in password field
                    requestBuilder.addHeader("X-API-Key", req.getAuthPassword());
                }
                break;
                
            default:
                System.err.println("WARNING: Unknown authentication type for GraphQL: " + authType);
        }
    }
}
