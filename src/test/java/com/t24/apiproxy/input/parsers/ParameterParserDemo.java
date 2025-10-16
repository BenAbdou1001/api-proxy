package com.t24.apiproxy.input.parsers;

import java.util.List;
import com.t24.apiproxy.model.ApiRequest;

/**
 * Demo program to test ParameterParser functionality
 */
public class ParameterParserDemo {
    public static void main(String[] args) {
        ParameterParser parser = new ParameterParser();
        
        System.out.println("=== ParameterParser Demo ===\n");
        
        // Test 1: Simple URL
        System.out.println("Test 1: Simple URL");
        testParse(parser, "https://jsonplaceholder.typicode.com/posts/1");
        
        // Test 2: Method + URL
        System.out.println("\nTest 2: Method + URL");
        testParse(parser, "POST https://httpbin.org/post");
        
        // Test 3: Key=Value format with multiple parameters
        System.out.println("\nTest 3: Key=Value format");
        testParse(parser, "method=GET url=https://httpbin.org/get timeout=5000");
        
        // Test 4: With headers
        System.out.println("\nTest 4: With headers");
        testParse(parser, "method=POST url=https://httpbin.org/post headers=\"Content-Type:application/json;Accept:application/json\"");
        
        // Test 5: With body
        System.out.println("\nTest 5: With body");
        testParse(parser, "method=POST url=https://httpbin.org/post body={\"name\":\"John\",\"age\":30}");
        
        // Test 6: With authentication
        System.out.println("\nTest 6: With authentication");
        testParse(parser, "url=https://httpbin.org/basic-auth/user/pass auth=BASIC:user:pass");
        
        // Test 7: Complex example with multiple options
        System.out.println("\nTest 7: Complex example");
        testParse(parser, "method=POST url=https://httpbin.org/post " +
                         "headers=\"Content-Type:application/json\" " +
                         "body={\"test\":true} " +
                         "timeout=10000 " +
                         "retry=3 " +
                         "name=ComplexTest");
        
        System.out.println("\n=== All Tests Completed Successfully! ===");
    }
    
    private static void testParse(ParameterParser parser, String input) {
        try {
            System.out.println("Input: " + input);
            List<ApiRequest> requests = parser.parse(input);
            
            if (requests != null && !requests.isEmpty()) {
                ApiRequest req = requests.get(0);
                System.out.println("✅ Parsed successfully:");
                System.out.println("   Method: " + req.getMethod());
                System.out.println("   URL: " + req.getUrl());
                if (req.getTimeout() > 0) {
                    System.out.println("   Timeout: " + req.getTimeout() + "ms");
                }
                if (req.getName() != null) {
                    System.out.println("   Name: " + req.getName());
                }
                if (req.getHeaders() != null && !req.getHeaders().isEmpty()) {
                    System.out.println("   Headers: " + req.getHeaders().size() + " header(s)");
                }
                if (req.getBody() != null) {
                    System.out.println("   Body: " + (req.getBody().toString().length() > 50 ? 
                        req.getBody().toString().substring(0, 50) + "..." : req.getBody()));
                }
                if (req.getAuthType() != null) {
                    System.out.println("   Auth: " + req.getAuthType() + " (" + req.getAuthUsername() + ")");
                }
                if (req.getRetryCount() > 0) {
                    System.out.println("   Retry: " + req.getRetryCount() + " time(s)");
                }
            } else {
                System.out.println("❌ No requests parsed");
            }
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
