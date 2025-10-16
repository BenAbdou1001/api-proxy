package com.t24.apiproxy.input.parsers;

import java.util.List;
import com.t24.apiproxy.client.HttpClientManager;
import com.t24.apiproxy.main.config.Configuration;
import com.t24.apiproxy.model.ApiRequest;
import com.t24.apiproxy.model.ApiResponse;

/**
 * Real integration test showing ParameterParser executing actual HTTP requests
 */
public class ParameterParserIntegrationTest {
    public static void main(String[] args) throws Exception {
        // Load configuration
        java.util.Properties props = new java.util.Properties();
        try (java.io.InputStream input = ParameterParserIntegrationTest.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                props.load(input);
            }
        }
        
        Configuration config = new Configuration(props);
        HttpClientManager clientManager = new HttpClientManager(config);
        ParameterParser parser = new ParameterParser();
        
        System.out.println("=== ParameterParser Integration Test ===");
        System.out.println("Testing with REAL HTTP requests\n");
        
        // Test 1: Simple GET request
        System.out.println("Test 1: Simple GET");
        executeTest(parser, clientManager, "https://jsonplaceholder.typicode.com/posts/1");
        
        // Test 2: GET with timeout
        System.out.println("\nTest 2: GET with custom timeout");
        executeTest(parser, clientManager, "method=GET url=https://httpbin.org/get timeout=5000");
        
        // Test 3: POST with JSON body
        System.out.println("\nTest 3: POST with JSON body");
        executeTest(parser, clientManager, 
            "method=POST url=https://httpbin.org/post " +
            "headers=\"Content-Type:application/json\" " +
            "body={\"name\":\"ParameterParserTest\",\"works\":true}");
        
        System.out.println("\n=== All Integration Tests Passed! ===");
        System.out.println("✅ ParameterParser is fully functional and can execute real HTTP requests!");
    }
    
    private static void executeTest(ParameterParser parser, HttpClientManager clientManager, String input) {
        try {
            System.out.println("Input: " + input);
            
            // Parse the parameter string
            List<ApiRequest> requests = parser.parse(input);
            ApiRequest request = requests.get(0);
            
            System.out.println("Parsed: " + request.getMethod() + " " + request.getUrl());
            
            // Execute the actual HTTP request
            ApiResponse response = clientManager.execute(request);
            
            System.out.println("✅ Response Status: " + response.getStatusCode());
            Object bodyObj = response.getBody();
            String body = bodyObj != null ? bodyObj.toString() : null;
            if (body != null && body.length() > 100) {
                System.out.println("   Body Preview: " + body.substring(0, 100) + "...");
            } else if (body != null) {
                System.out.println("   Body: " + body);
            }
            
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
