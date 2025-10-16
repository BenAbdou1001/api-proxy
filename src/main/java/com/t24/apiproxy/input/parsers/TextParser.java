package com.t24.apiproxy.input.parsers;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.t24.apiproxy.exception.InputProcessingException;
import com.t24.apiproxy.model.ApiRequest;

/**
 * TextParser parses text files containing HTTP request definitions.
 * 
 * Supported formats:
 * 1. Simple format (one request per block, separated by blank lines):
 *    GET https://api.example.com/endpoint
 *    -H "Content-Type: application/json"
 *    -d '{"key":"value"}'
 * 
 * 2. cURL commands:
 *    curl -X POST https://api.example.com/endpoint \
 *      -H "Content-Type: application/json" \
 *      -d '{"key":"value"}' \
 *      -u username:password
 * 
 * 3. HTTP message format:
 *    POST /api/users HTTP/1.1
 *    Host: api.example.com
 *    Content-Type: application/json
 *    
 *    {"name":"John","email":"john@example.com"}
 * 
 * Each request block should be separated by one or more blank lines.
 */
public class TextParser implements Parser {
    private static final Pattern METHOD_URL_PATTERN = Pattern.compile("(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS)\\s+(\\S+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern HEADER_PATTERN = Pattern.compile("-H\\s+['\"]?([^:'\"]+):\\s*([^'\"]+)['\"]?");
    private static final Pattern DATA_PATTERN = Pattern.compile("(-d|--data|--data-raw)\\s+['\"]?(.+?)['\"]?(\\s|$)");
    private static final Pattern COOKIE_PATTERN = Pattern.compile("--cookie\\s+['\"]?([^'\"]+)['\"]?");
    private static final Pattern AUTH_PATTERN = Pattern.compile("-u\\s+([^:]+):([^\\s]+)");
    private static final Pattern CURL_PATTERN = Pattern.compile("curl\\s+(?:-X\\s+(\\w+)\\s+)?['\"]?(https?://\\S+)['\"]?");
    private static final Pattern HTTP_REQUEST_LINE = Pattern.compile("(GET|POST|PUT|DELETE|PATCH|HEAD|OPTIONS)\\s+([^\\s]+)\\s+HTTP/[\\d.]+", Pattern.CASE_INSENSITIVE);
    private static final Pattern HTTP_HEADER_LINE = Pattern.compile("([^:]+):\\s*(.+)");
    private static final Pattern TIMEOUT_PATTERN = Pattern.compile("--max-time\\s+(\\d+)");
    
    private final boolean strictMode;
    private final boolean ignoreInvalidBlocks;
    
    /**
     * Default constructor with standard parsing rules.
     * - Not strict: allows flexible formats
     * - Doesn't ignore invalid blocks: throws exception on parse errors
     */
    public TextParser() {
        this(false, false);
    }
    
    /**
     * Constructor with custom parsing options.
     * 
     * @param strictMode If true, enforces strict format validation
     * @param ignoreInvalidBlocks If true, skips invalid blocks instead of throwing exceptions
     */
    public TextParser(boolean strictMode, boolean ignoreInvalidBlocks) {
        this.strictMode = strictMode;
        this.ignoreInvalidBlocks = ignoreInvalidBlocks;
    }

    @Override
    public List<ApiRequest> parse(String path) throws IOException {
        // Validate file exists
        if (!Files.exists(Paths.get(path))) {
            throw InputProcessingException.forMissingFile(path);
        }
        
        List<ApiRequest> requests = new ArrayList<>();
        List<String> lines;
        
        try {
            lines = Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            throw InputProcessingException.forFile(path, 0, "Failed to read file: " + e.getMessage());
        }
        
        StringBuilder block = new StringBuilder();
        int blockStartLine = 1;
        int currentLine = 1;

        for (String line : lines) {
            if (line.isBlank()) {
                if (block.length() > 0) {
                    try {
                        ApiRequest req = parseRequestBlock(block.toString(), path, blockStartLine);
                        if (req != null) {
                            requests.add(req);
                        }
                    } catch (InputProcessingException e) {
                        if (!ignoreInvalidBlocks) {
                            throw e;
                        }
                        // Otherwise skip this block
                    }
                    block.setLength(0);
                    blockStartLine = currentLine + 1;
                }
            } else {
                block.append(line).append("\n");
            }
            currentLine++;
        }
        
        // Process last block if exists
        if (block.length() > 0) {
            try {
                ApiRequest req = parseRequestBlock(block.toString(), path, blockStartLine);
                if (req != null) {
                    requests.add(req);
                }
            } catch (InputProcessingException e) {
                if (!ignoreInvalidBlocks) {
                    throw e;
                }
            }
        }
        
        if (requests.isEmpty()) {
            throw InputProcessingException.forFile(
                path,
                0,
                "No valid HTTP requests found in file"
            );
        }
        
        return requests;
    }

    private ApiRequest parseRequestBlock(String block, String filePath, int lineNumber) {
        try {
            ApiRequest.Builder builder = ApiRequest.newBuilder();
            
            // Detect format and parse accordingly
            if (block.trim().startsWith("curl")) {
                return parseCurlCommand(block, builder, filePath, lineNumber);
            } else if (HTTP_REQUEST_LINE.matcher(block).find()) {
                return parseHttpMessage(block, builder, filePath, lineNumber);
            } else {
                return parseSimpleFormat(block, builder, filePath, lineNumber);
            }
            
        } catch (Exception e) {
            if (ignoreInvalidBlocks) {
                return null; // Skip invalid block
            }
            throw InputProcessingException.forFile(
                filePath,
                lineNumber,
                "Failed to parse request block: " + e.getMessage()
            );
        }
    }
    
    /**
     * Parses simple format (METHOD URL followed by options).
     */
    private ApiRequest parseSimpleFormat(String block, ApiRequest.Builder builder, String filePath, int lineNumber) {
        try {
            // Method and URL
            Matcher m = METHOD_URL_PATTERN.matcher(block);
            if (m.find()) {
                builder.method(m.group(1).toUpperCase());
                builder.url(URI.create(m.group(2)).toURL());
            } else {
                if (strictMode) {
                    throw InputProcessingException.forFile(
                        filePath,
                        lineNumber,
                        "Invalid request format: missing METHOD and URL"
                    );
                }
                return null;
            }

            // Headers
            parseHeaders(block, builder);
            
            // Data/body
            parseData(block, builder);
            
            // Cookies
            parseCookies(block, builder);
            
            // Auth
            parseAuth(block, builder);
            
            // Timeout
            parseTimeout(block, builder);
            
            // Set defaults
            setDefaults(builder);

            return builder.build();
        } catch (Exception e) {
            if (e instanceof InputProcessingException) {
                throw (InputProcessingException) e;
            }
            throw InputProcessingException.forFile(
                filePath,
                lineNumber,
                "Error parsing simple format: " + e.getMessage()
            );
        }
    }
    
    /**
     * Parses cURL command format.
     */
    private ApiRequest parseCurlCommand(String block, ApiRequest.Builder builder, String filePath, int lineNumber) {
        try {
            // Extract method and URL from curl command
            Matcher curlMatcher = CURL_PATTERN.matcher(block);
            if (curlMatcher.find()) {
                String method = curlMatcher.group(1);
                String url = curlMatcher.group(2);
                
                builder.method(method != null ? method.toUpperCase() : "GET");
                builder.url(URI.create(url).toURL());
            } else {
                throw InputProcessingException.forFile(
                    filePath,
                    lineNumber,
                    "Invalid cURL command: missing URL"
                );
            }
            
            // Headers
            parseHeaders(block, builder);
            
            // Data/body
            parseData(block, builder);
            
            // Cookies
            parseCookies(block, builder);
            
            // Auth
            parseAuth(block, builder);
            
            // Timeout
            parseTimeout(block, builder);
            
            // Set defaults
            setDefaults(builder);
            
            return builder.build();
        } catch (Exception e) {
            if (e instanceof InputProcessingException) {
                throw (InputProcessingException) e;
            }
            throw InputProcessingException.forFile(
                filePath,
                lineNumber,
                "Error parsing cURL command: " + e.getMessage()
            );
        }
    }
    
    /**
     * Parses HTTP message format (RFC 7230).
     */
    private ApiRequest parseHttpMessage(String block, ApiRequest.Builder builder, String filePath, int lineNumber) {
        try {
            String[] lines = block.split("\n");
            int i = 0;
            
            // Parse request line
            Matcher reqLine = HTTP_REQUEST_LINE.matcher(lines[i++]);
            if (!reqLine.matches()) {
                throw InputProcessingException.forFile(
                    filePath,
                    lineNumber,
                    "Invalid HTTP request line"
                );
            }
            
            String method = reqLine.group(1).toUpperCase();
            String path = reqLine.group(2);
            builder.method(method);
            
            // Parse headers
            String host = null;
            StringBuilder bodyBuilder = new StringBuilder();
            boolean inBody = false;
            
            while (i < lines.length) {
                String line = lines[i++];
                
                if (line.trim().isEmpty()) {
                    inBody = true;
                    continue;
                }
                
                if (inBody) {
                    bodyBuilder.append(line).append("\n");
                } else {
                    Matcher headerMatcher = HTTP_HEADER_LINE.matcher(line);
                    if (headerMatcher.matches()) {
                        String headerName = headerMatcher.group(1).trim();
                        String headerValue = headerMatcher.group(2).trim();
                        
                        if (headerName.equalsIgnoreCase("Host")) {
                            host = headerValue;
                        } else {
                            builder.addHeader(headerName, headerValue);
                        }
                    }
                }
            }
            
            // Construct full URL
            if (host != null) {
                String scheme = "https"; // Default to https
                String fullUrl = scheme + "://" + host + path;
                builder.url(URI.create(fullUrl).toURL());
            } else {
                throw InputProcessingException.forFile(
                    filePath,
                    lineNumber,
                    "HTTP message format requires Host header"
                );
            }
            
            // Set body if present
            String body = bodyBuilder.toString().trim();
            if (!body.isEmpty()) {
                builder.body(body);
            }
            
            // Set defaults
            setDefaults(builder);
            
            return builder.build();
        } catch (Exception e) {
            if (e instanceof InputProcessingException) {
                throw (InputProcessingException) e;
            }
            throw InputProcessingException.forFile(
                filePath,
                lineNumber,
                "Error parsing HTTP message: " + e.getMessage()
            );
        }
    }
    
    /**
     * Parses headers from block.
     */
    private void parseHeaders(String block, ApiRequest.Builder builder) {
        Matcher headerMatcher = HEADER_PATTERN.matcher(block);
        while (headerMatcher.find()) {
            builder.addHeader(headerMatcher.group(1).trim(), headerMatcher.group(2).trim());
        }
    }
    
    /**
     * Parses data/body from block.
     */
    private void parseData(String block, ApiRequest.Builder builder) {
        Matcher dataMatcher = DATA_PATTERN.matcher(block);
        if (dataMatcher.find()) {
            String data = dataMatcher.group(2).trim();
            // Remove quotes if present
            if ((data.startsWith("'") && data.endsWith("'")) ||
                (data.startsWith("\"") && data.endsWith("\""))) {
                data = data.substring(1, data.length() - 1);
            }
            builder.body(data);
        }
    }
    
    /**
     * Parses cookies from block.
     */
    private void parseCookies(String block, ApiRequest.Builder builder) {
        Matcher cookieMatcher = COOKIE_PATTERN.matcher(block);
        if (cookieMatcher.find()) {
            builder.cookies(cookieMatcher.group(1).trim());
        }
    }
    
    /**
     * Parses authentication from block.
     */
    private void parseAuth(String block, ApiRequest.Builder builder) {
        Matcher authMatcher = AUTH_PATTERN.matcher(block);
        if (authMatcher.find()) {
            builder.authType("BASIC")
                   .authUsername(authMatcher.group(1))
                   .authPassword(authMatcher.group(2));
        }
    }
    
    /**
     * Parses timeout from block.
     */
    private void parseTimeout(String block, ApiRequest.Builder builder) {
        Matcher timeoutMatcher = TIMEOUT_PATTERN.matcher(block);
        if (timeoutMatcher.find()) {
            builder.timeout(Integer.parseInt(timeoutMatcher.group(1)) * 1000); // Convert to ms
        }
    }
    
    /**
     * Sets default values for optional fields.
     */
    private void setDefaults(ApiRequest.Builder builder) {
        // Default timeout of 30 seconds if not specified
        // Note: This is handled by the request execution layer if needed
    }
}
