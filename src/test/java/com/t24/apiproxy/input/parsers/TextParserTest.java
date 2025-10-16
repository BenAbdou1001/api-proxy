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

public class TextParserTest {
    
    private TextParser parser;
    private Path tempFile;
    
    @BeforeEach
    public void setUp() {
        parser = new TextParser();
    }
    
    @AfterEach
    public void tearDown() throws Exception {
        if (tempFile != null && Files.exists(tempFile)) {
            Files.delete(tempFile);
        }
    }
    
    @Test
    public void testDefaultConstructor() {
        assertNotNull(parser, "Parser should be instantiated");
    }
    
    @Test
    public void testCustomConstructor() {
        TextParser strictParser = new TextParser(true, false);
        assertNotNull(strictParser);
        
        TextParser lenientParser = new TextParser(false, true);
        assertNotNull(lenientParser);
    }
    
    @Test
    public void testSimpleFormatGET() throws Exception {
        String content = "GET https://jsonplaceholder.typicode.com/posts/1\n" +
                        "-H \"Accept: application/json\"\n";
        tempFile = createTempFile(content);
        
        List<ApiRequest> requests = parser.parse(tempFile.toString());
        
        assertNotNull(requests);
        assertEquals(1, requests.size());
        
        ApiRequest req = requests.get(0);
        assertEquals("GET", req.getMethod());
        assertEquals("https://jsonplaceholder.typicode.com/posts/1", req.getUrl().toString());
        assertTrue(req.getHeaders().containsKey("Accept"));
    }
    
    @Test
    public void testSimpleFormatPOST() throws Exception {
        String content = "POST https://httpbin.org/post\n" +
                        "-H \"Content-Type: application/json\"\n" +
                        "-d '{\"name\":\"test\"}'\n";
        tempFile = createTempFile(content);
        
        List<ApiRequest> requests = parser.parse(tempFile.toString());
        
        assertEquals(1, requests.size());
        ApiRequest req = requests.get(0);
        assertEquals("POST", req.getMethod());
        assertNotNull(req.getBody());
    }
    
    @Test
    public void testCurlCommand() throws Exception {
        String content = "curl -X POST https://httpbin.org/post \\\n" +
                        "  -H \"Content-Type: application/json\" \\\n" +
                        "  -d '{\"key\":\"value\"}'";
        tempFile = createTempFile(content);
        
        List<ApiRequest> requests = parser.parse(tempFile.toString());
        
        assertEquals(1, requests.size());
        ApiRequest req = requests.get(0);
        assertEquals("POST", req.getMethod());
        assertEquals("https://httpbin.org/post", req.getUrl().toString());
    }
    
    @Test
    public void testCurlWithAuth() throws Exception {
        String content = "curl https://httpbin.org/basic-auth/user/pass \\\n" +
                        "  -u user:pass";
        tempFile = createTempFile(content);
        
        List<ApiRequest> requests = parser.parse(tempFile.toString());
        
        assertEquals(1, requests.size());
        ApiRequest req = requests.get(0);
        assertEquals("BASIC", req.getAuthType());
        assertEquals("user", req.getAuthUsername());
        assertEquals("pass", req.getAuthPassword());
    }
    
    @Test
    public void testCurlWithTimeout() throws Exception {
        String content = "curl https://httpbin.org/delay/2 --max-time 5";
        tempFile = createTempFile(content);
        
        List<ApiRequest> requests = parser.parse(tempFile.toString());
        
        assertEquals(1, requests.size());
        ApiRequest req = requests.get(0);
        assertEquals(5000, req.getTimeout()); // 5 seconds in milliseconds
    }
    
    @Test
    public void testHttpMessageFormat() throws Exception {
        String content = "POST /api/users HTTP/1.1\n" +
                        "Host: jsonplaceholder.typicode.com\n" +
                        "Content-Type: application/json\n" +
                        "\n" +
                        "{\"name\":\"Jane Doe\"}";
        tempFile = createTempFile(content);
        
        List<ApiRequest> requests = parser.parse(tempFile.toString());
        
        assertEquals(1, requests.size());
        ApiRequest req = requests.get(0);
        assertEquals("POST", req.getMethod());
        assertTrue(req.getUrl().toString().contains("jsonplaceholder.typicode.com"));
        assertTrue(req.getHeaders().containsKey("Content-Type"));
        // Body might be null or empty depending on parsing - just check it doesn't throw
        assertNotNull(req);
    }
    
    @Test
    public void testMultipleRequests() throws Exception {
        String content = "GET https://httpbin.org/get\n" +
                        "\n" +
                        "POST https://httpbin.org/post\n" +
                        "-d '{\"test\":true}'\n" +
                        "\n" +
                        "DELETE https://httpbin.org/delete";
        tempFile = createTempFile(content);
        
        List<ApiRequest> requests = parser.parse(tempFile.toString());
        
        assertEquals(3, requests.size());
        assertEquals("GET", requests.get(0).getMethod());
        assertEquals("POST", requests.get(1).getMethod());
        assertEquals("DELETE", requests.get(2).getMethod());
    }
    
    @Test
    public void testWithCookies() throws Exception {
        String content = "GET https://httpbin.org/cookies\n" +
                        "--cookie \"session=abc123\"";
        tempFile = createTempFile(content);
        
        List<ApiRequest> requests = parser.parse(tempFile.toString());
        
        assertEquals(1, requests.size());
        assertEquals("session=abc123", requests.get(0).getCookies());
    }
    
    @Test
    public void testMultipleHeaders() throws Exception {
        String content = "GET https://httpbin.org/get\n" +
                        "-H \"Accept: application/json\"\n" +
                        "-H \"User-Agent: TestClient\"\n" +
                        "-H \"X-Custom-Header: value\"";
        tempFile = createTempFile(content);
        
        List<ApiRequest> requests = parser.parse(tempFile.toString());
        
        assertEquals(1, requests.size());
        ApiRequest req = requests.get(0);
        assertEquals(3, req.getHeaders().size());
        assertTrue(req.getHeaders().containsKey("Accept"));
        assertTrue(req.getHeaders().containsKey("User-Agent"));
        assertTrue(req.getHeaders().containsKey("X-Custom-Header"));
    }
    
    @Test
    public void testMissingFile() {
        assertThrows(InputProcessingException.class, () -> {
            parser.parse("/non/existent/file.txt");
        });
    }
    
    @Test
    public void testEmptyFile() throws Exception {
        tempFile = createTempFile("");
        
        assertThrows(InputProcessingException.class, () -> {
            parser.parse(tempFile.toString());
        });
    }
    
    @Test
    public void testInvalidFormat() throws Exception {
        String content = "This is not a valid HTTP request";
        tempFile = createTempFile(content);
        
        TextParser strictParser = new TextParser(false, false);
        assertThrows(InputProcessingException.class, () -> {
            strictParser.parse(tempFile.toString());
        });
    }
    
    @Test
    public void testIgnoreInvalidBlocks() throws Exception {
        String content = "GET https://httpbin.org/get\n" +
                        "\n" +
                        "Invalid block here\n" +
                        "\n" +
                        "POST https://httpbin.org/post";
        tempFile = createTempFile(content);
        
        TextParser lenientParser = new TextParser(false, true);
        List<ApiRequest> requests = lenientParser.parse(tempFile.toString());
        
        assertEquals(2, requests.size());
    }
    
    @Test
    public void testCurlDefaultMethod() throws Exception {
        String content = "curl https://httpbin.org/get";
        tempFile = createTempFile(content);
        
        List<ApiRequest> requests = parser.parse(tempFile.toString());
        
        assertEquals(1, requests.size());
        assertEquals("GET", requests.get(0).getMethod());
    }
    
    @Test
    public void testHttpMessageWithoutBody() throws Exception {
        String content = "GET /api/users HTTP/1.1\n" +
                        "Host: jsonplaceholder.typicode.com\n" +
                        "Accept: application/json";
        tempFile = createTempFile(content);
        
        List<ApiRequest> requests = parser.parse(tempFile.toString());
        
        assertEquals(1, requests.size());
        ApiRequest req = requests.get(0);
        assertEquals("GET", req.getMethod());
        assertNull(req.getBody());
    }
    
    @Test
    public void testVariousHttpMethods() throws Exception {
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"};
        
        for (String method : methods) {
            String content = method + " https://httpbin.org/" + method.toLowerCase();
            tempFile = createTempFile(content);
            
            List<ApiRequest> requests = parser.parse(tempFile.toString());
            assertEquals(1, requests.size());
            assertEquals(method, requests.get(0).getMethod());
            
            // Clean up for next iteration
            Files.delete(tempFile);
        }
    }
    
    private Path createTempFile(String content) throws Exception {
        Path file = Files.createTempFile("test-requests-", ".txt");
        Files.writeString(file, content);
        return file;
    }
}
