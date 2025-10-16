package com.t24.apiproxy.model;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {
    // Response Information
    private int statusCode;
    private String statusMessage; // e.g., "OK", "Not Found"
    private Map<String, String> headers = new HashMap<>();
    private String cookies; // Cookies: name1=value1;name2=value2

    // Response Body
    private Object body; // Raw response body (JSON, XML, plain text, etc.)
    private String contentType; // application/json, text/html, etc.
    private long contentLength;

    // Connection / Timing Info
    private long responseTime; // total time in ms
    private long receivedAt;   // timestamp when received

    // Error Handling
    private String errorMessage; // if exception or error occurred
    private boolean success;     // convenience flag

    // Metadata (custom info, logs, etc.)
    private Metadata meta = new Metadata();

    // Getters
    public int getStatusCode() { return statusCode; }
    public String getStatusMessage() { return statusMessage; }
    public Map<String, String> getHeaders() { return headers; }
    public String getCookies() { return cookies; }
    public Object getBody() { return body; }
    public String getContentType() { return contentType; }
    public long getContentLength() { return contentLength; }
    public long getResponseTime() { return responseTime; }
    public long getReceivedAt() { return receivedAt; }
    public String getErrorMessage() { return errorMessage; }
    public boolean isSuccess() { return success; }
    public Metadata getMeta() { return meta; }

    // Builder
    public static Builder newBuilder() { return new Builder(); }

    public static class Builder {
        private final ApiResponse res = new ApiResponse();

        public Builder statusCode(int code) { res.statusCode = code; return this; }
        public Builder statusMessage(String msg) { res.statusMessage = msg; return this; }
        public Builder addHeader(String key, String value) { res.headers.put(key, value); return this; }
        public Builder headers(Map<String, String> headers) { res.headers.putAll(headers); return this; }
        public Builder cookies(String cookies) { res.cookies = cookies; return this; }
        public Builder body(Object body) { res.body = body; return this; }
        public Builder contentType(String type) { res.contentType = type; return this; }
        public Builder contentLength(long length) { res.contentLength = length; return this; }
        public Builder responseTime(long time) { res.responseTime = time; return this; }
        public Builder receivedAt(long timestamp) { res.receivedAt = timestamp; return this; }
        public Builder errorMessage(String error) { res.errorMessage = error; return this; }
        public Builder success(boolean success) { res.success = success; return this; }
        public Builder meta(Metadata meta) { res.meta = meta; return this; }

        public ApiResponse build() { return res; }
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "statusCode=" + statusCode +
                ", statusMessage='" + statusMessage + '\'' +
                ", headers=" + headers +
                ", cookies='" + cookies + '\'' +
                ", body=" + body +
                ", contentType='" + contentType + '\'' +
                ", contentLength=" + contentLength +
                ", responseTime=" + responseTime +
                ", receivedAt=" + receivedAt +
                ", errorMessage='" + errorMessage + '\'' +
                ", success=" + success +
                ", meta=" + meta +
                '}';
    }
}
