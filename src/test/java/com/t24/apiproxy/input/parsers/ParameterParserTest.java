package com.t24.apiproxy.input.parsers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.t24.apiproxy.exception.InputProcessingException;
import com.t24.apiproxy.model.ApiRequest;

public class ParameterParserTest {
    
    private ParameterParser parser;
    private Path tempFile;
    
    @BeforeEach
    public void setUp() {
        parser = new ParameterParser();
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        if (tempFile != null && Files.exists(tempFile)) {
            Files.delete(tempFile);
        }
    }
    
    @Test
    public void testConstructor() {
        assertNotNull(parser, "Parser should be instantiated");
    }
    
    @Test
    public void testSimpleUrl() throws Exception {
        String input = "https://jsonplaceholder.typicode.com/posts/1";
        List<ApiRequest> requests = parser.parse(input);
        
        assertNotNull(requests);
        assertEquals(1, requests.size());
        
        ApiRequest req = requests.get(0);
        assertEquals("GET", req.getMethod());
        assertEquals("https://jsonplaceholder.typicode.com/posts/1", req.getUrl().toString());
        assertEquals(30000, req.getTimeout());
    }
    
    @Test
    public void testMethodUrl() throws Exception {
        String input = "POST https://httpbin.org/post";
        List<ApiRequest> requests = parser.parse(input);
        
        assertNotNull(requests);
        assertEquals(1, requests.size());
        
        ApiRequest req = requests.get(0);
        assertEquals("POST", req.getMethod());
        assertEquals("https://httpbin.org/post", req.getUrl().toString());
    }
    
    @Test
    public void testKeyValueFormat() throws Exception {
        String input = "method=GET url=https://httpbin.org/get timeout=5000";
        List<ApiRequest> requests = parser.parse(input);
        
        assertNotNull(requests);
        assertEquals(1, requests.size());
        
        ApiRequest req = requests.get(0);
        assertEquals("GET", req.getMethod());
        assertEquals("https://httpbin.org/get", req.getUrl().toString());
        assertEquals(5000, req.getTimeout());
    }
    
    @Test
    public void testWithHeaders() throws Exception {
        String input = "method=POST url=https://httpbin.org/post headers=\"Content-Type:application/json;Accept:application/json\"";
        List<ApiRequest> requests = parser.parse(input);
        
        assertNotNull(requests);
        assertEquals(1, requests.size());
        
        ApiRequest req = requests.get(0);
        assertEquals("POST", req.getMethod());
        assertTrue(req.getHeaders().containsKey("Content-Type"));
        assertEquals("application/json", req.getHeaders().get("Content-Type"));
        assertTrue(req.getHeaders().containsKey("Accept"));
        assertEquals("application/json", req.getHeaders().get("Accept"));
    }
    
    @Test
    public void testWithBody() throws Exception {
        String input = "method=POST url=https://httpbin.org/post body={\"key\":\"value\"}";
        List<ApiRequest> requests = parser.parse(input);
        
        assertNotNull(requests);
        assertEquals(1, requests.size());
        
        ApiRequest req = requests.get(0);
        assertEquals("POST", req.getMethod());
        assertNotNull(req.getBody());
        assertEquals("{\"key\":\"value\"}", req.getBody().toString());
    }
    
    @Test
    public void testWithAuth() throws Exception {
        String input = "url=https://httpbin.org/basic-auth/user/pass auth=BASIC:user:pass";
        List<ApiRequest> requests = parser.parse(input);
        
        assertNotNull(requests);
        assertEquals(1, requests.size());
        
        ApiRequest req = requests.get(0);
        assertEquals("BASIC", req.getAuthType());
        assertEquals("user", req.getAuthUsername());
        assertEquals("pass", req.getAuthPassword());
    }
    
    @Test
    public void testWithCookies() throws Exception {
        String input = "url=https://httpbin.org/cookies cookies=\"session=abc123;token=xyz789\"";
        List<ApiRequest> requests = parser.parse(input);
        
        assertNotNull(requests);
        assertEquals(1, requests.size());
        
        ApiRequest req = requests.get(0);
        assertEquals("session=abc123;token=xyz789", req.getCookies());
    }
    
    @Test
    public void testWithProxy() throws Exception {
        String input = "url=https://httpbin.org/get proxy=proxy.example.com:8080:user:pass";
        List<ApiRequest> requests = parser.parse(input);
        
        assertNotNull(requests);
        assertEquals(1, requests.size());
        
        ApiRequest req = requests.get(0);
        assertEquals("proxy.example.com", req.getProxyHost());
        assertEquals(8080, req.getProxyPort());
        assertEquals("user", req.getProxyUsername());
        assertEquals("pass", req.getProxyPassword());
    }
    
    @Test
    public void testWithAllOptions() throws Exception {
        String input = "method=POST url=https://httpbin.org/post " +
                      "headers=\"Content-Type:application/json\" " +
                      "body={\"test\":true} " +
                      "timeout=10000 " +
                      "retry=3 " +
                      "name=TestRequest " +
                      "follow=true " +
                      "verify=false";
        List<ApiRequest> requests = parser.parse(input);
        
        assertNotNull(requests);
        assertEquals(1, requests.size());
        
        ApiRequest req = requests.get(0);
        assertEquals("POST", req.getMethod());
        assertEquals("https://httpbin.org/post", req.getUrl().toString());
        assertEquals(10000, req.getTimeout());
        assertEquals(3, req.getRetryCount());
        assertEquals("TestRequest", req.getName());
        assertTrue(req.isFollowRedirects());
        assertFalse(req.isVerifySsl());
    }
    
    @Test
    public void testNullInput() {
        assertThrows(InputProcessingException.class, () -> {
            parser.parse(null);
        });
    }
    
    @Test
    public void testEmptyInput() {
        assertThrows(InputProcessingException.class, () -> {
            parser.parse("");
        });
    }
    
    @Test
    public void testMissingUrl() {
        assertThrows(InputProcessingException.class, () -> {
            parser.parse("method=GET timeout=5000");
        });
    }
    
    @Test
    public void testInvalidTimeout() {
        assertThrows(InputProcessingException.class, () -> {
            parser.parse("url=https://example.com timeout=invalid");
        });
    }
    
    @Test
    public void testCaseInsensitiveMethod() throws Exception {
        String input = "method=post url=https://httpbin.org/post";
        List<ApiRequest> requests = parser.parse(input);
        
        ApiRequest req = requests.get(0);
        assertEquals("POST", req.getMethod());
    }
    
    @Test
    public void testDefaultMethodIsGet() throws Exception {
        String input = "url=https://httpbin.org/get";
        List<ApiRequest> requests = parser.parse(input);
        
        ApiRequest req = requests.get(0);
        assertEquals("GET", req.getMethod());
    }
    
    @Test
    public void testQuotedValues() throws Exception {
        String input = "url=https://httpbin.org/post body=\"{\\\"key\\\":\\\"value\\\"}\"";
        List<ApiRequest> requests = parser.parse(input);
        
        ApiRequest req = requests.get(0);
        assertNotNull(req.getBody());
    }
}
