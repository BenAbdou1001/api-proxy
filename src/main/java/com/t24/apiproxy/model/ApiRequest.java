package com.t24.apiproxy.model;

import java.net.URL;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class ApiRequest {
    // General Information
    private String name; // Test case name/identifier
    private String method; // HTTP method
    private URL url; // Complete URL or base URL

    // Request Type
    private String requestType; // REST, GRAPHQL, SOAP, etc.

    // Query Parameters
    private Map<String, String> queryParams = new HashMap<>();

    // Request Body & Content
    private Object body; // Raw request body (JSON, XML, plain text)
    private String formData; // Form data: key1=value1&key2=value2
    private String multipartData; // File uploads: field1=@/path/to/file1.txt&field2=value2

    // Headers
    private Map<String, String> headers = new HashMap<>();
    private String cookies; // Cookies: name1=value1;name2=value2

    // Authentication
    private String authType; // BASIC, BEARER, API_KEY, OAUTH2, DIGEST, NTLM
    private String authUsername;
    private String authPassword;

    // Connection Settings
    private int timeout; // Request timeout in ms
    private int connectionTimeout;
    private int readTimeout;
    private boolean followRedirects;
    private int maxRedirects;
    private boolean verifySsl;

    // Proxy Settings
    private String proxyHost;
    private int proxyPort;
    private String proxyUsername;
    private String proxyPassword;

    // Response Validation
    private String expectedStatus; // Expected HTTP status code(s)
    private String validateResponse; // JSON path or regex
    private String outputFile; // File path to save response

    // Retry Logic
    private int retryCount;
    private int retryDelay;

    // Getters
    public String getName() {
        return name;
    }

    public String getMethod() {
        return method;
    }

    public URL getUrl() {
        return url;
    }

    public String getRequestType() {
        return requestType;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Object getBody() {
        return body;
    }

    public String getFormData() {
        return formData;
    }

    public String getMultipartData() {
        return multipartData;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getCookies() {
        return cookies;
    }

    public String getAuthType() {
        return authType;
    }

    public String getAuthUsername() {
        return authUsername;
    }

    public String getAuthPassword() {
        return authPassword;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public int getMaxRedirects() {
        return maxRedirects;
    }

    public boolean isVerifySsl() {
        return verifySsl;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyUsername() {
        return proxyUsername;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public String getExpectedStatus() {
        return expectedStatus;
    }

    public String getValidateResponse() {
        return validateResponse;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public HttpRequest toHttpRequest() {
        HttpRequest.Builder httpBuilder = HttpRequest.newBuilder();

        // Set URI
        if (this.url != null) {
            try {
                httpBuilder.uri(this.url.toURI());
            } catch (java.net.URISyntaxException e) {
                throw new RuntimeException("Invalid URL syntax: " + this.url, e);
            }
        }

        // Set method and body
        String method = this.method != null ? this.method.toUpperCase() : "GET";
        HttpRequest.BodyPublisher bodyPublisher;

        if (this.body != null) {
            bodyPublisher = HttpRequest.BodyPublishers.ofString(this.body.toString());
        } else if (this.formData != null) {
            bodyPublisher = HttpRequest.BodyPublishers.ofString(this.formData);
        } else if (this.multipartData != null) {
            bodyPublisher = HttpRequest.BodyPublishers.ofString(this.multipartData);
        } else {
            bodyPublisher = HttpRequest.BodyPublishers.noBody();
        }
        httpBuilder.method(method, bodyPublisher);

        // Set headers
        if (this.headers != null) {
            this.headers.forEach(httpBuilder::header);
        }

        // Set cookies
        if (this.cookies != null && !this.cookies.isEmpty()) {
            httpBuilder.header("Cookie", this.cookies);
        }

        // Set timeouts
        if (this.timeout > 0) {
            httpBuilder.timeout(java.time.Duration.ofMillis(this.timeout));
        }

        return httpBuilder.build();
    }

    // Builder
    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private final ApiRequest req = new ApiRequest();

        public Builder name(String name) {
            req.name = name;
            return this;
        }

        public Builder method(String method) {
            req.method = method;
            return this;
        }

        public Builder url(URL url) {
            req.url = url;
            return this;
        }

        public Builder requestType(String requestType) {
            req.requestType = requestType;
            return this;
        }

        public Builder addQueryParam(String name, String value) {
            req.queryParams.put(name, value);
            return this;
        }

        public Builder queryParams(Map<String, String> params) {
            req.queryParams.putAll(params);
            return this;
        }

        public Builder body(Object body) {
            req.body = body;
            return this;
        }

        public Builder formData(String formData) {
            req.formData = formData;
            return this;
        }

        public Builder multipartData(String multipartData) {
            req.multipartData = multipartData;
            return this;
        }

        public Builder addHeader(String name, String value) {
            req.headers.put(name, value);
            return this;
        }

        public Builder cookies(String cookies) {
            req.cookies = cookies;
            return this;
        }

        public Builder authType(String authType) {
            req.authType = authType;
            return this;
        }

        public Builder authUsername(String username) {
            req.authUsername = username;
            return this;
        }

        public Builder authPassword(String password) {
            req.authPassword = password;
            return this;
        }

        public Builder timeout(int timeout) {
            req.timeout = timeout;
            return this;
        }

        public Builder connectionTimeout(int connectionTimeout) {
            req.connectionTimeout = connectionTimeout;
            return this;
        }

        public Builder readTimeout(int readTimeout) {
            req.readTimeout = readTimeout;
            return this;
        }

        public Builder followRedirects(boolean followRedirects) {
            req.followRedirects = followRedirects;
            return this;
        }

        public Builder maxRedirects(int maxRedirects) {
            req.maxRedirects = maxRedirects;
            return this;
        }

        public Builder verifySsl(boolean verifySsl) {
            req.verifySsl = verifySsl;
            return this;
        }

        public Builder proxyHost(String proxyHost) {
            req.proxyHost = proxyHost;
            return this;
        }

        public Builder proxyPort(int proxyPort) {
            req.proxyPort = proxyPort;
            return this;
        }

        public Builder proxyUsername(String proxyUsername) {
            req.proxyUsername = proxyUsername;
            return this;
        }

        public Builder proxyPassword(String proxyPassword) {
            req.proxyPassword = proxyPassword;
            return this;
        }

        public Builder expectedStatus(String expectedStatus) {
            req.expectedStatus = expectedStatus;
            return this;
        }

        public Builder validateResponse(String validateResponse) {
            req.validateResponse = validateResponse;
            return this;
        }

        public Builder outputFile(String outputFile) {
            req.outputFile = outputFile;
            return this;
        }

        public Builder retryCount(int retryCount) {
            req.retryCount = retryCount;
            return this;
        }

        public Builder retryDelay(int retryDelay) {
            req.retryDelay = retryDelay;
            return this;
        }

        public ApiRequest build() {
            return req;
        }
    }
}
