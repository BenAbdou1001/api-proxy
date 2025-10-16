package com.t24.apiproxy.model;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {
    // Basic error info
    private String errorCode;       // App-specific or API error code
    private String statusMessage;   // e.g., "Bad Request", "Unauthorized"
    private int statusCode;         // HTTP status code (400, 401, 500...)

    // Messages
    private String userMessage;     // Friendly message for clients/UI
    private String technicalMessage;// Detailed message for logs/devs
    private Map<String, String> details = new HashMap<>(); // Extra info

    // Trace info
    private String executionId;     // Useful for correlation/tracing
    private String timestamp;       // ISO-8601 string (UTC preferred)
    private String path;            // The endpoint that caused the error

    // Getters
    public String getErrorCode() { return errorCode; }
    public String getStatusMessage() { return statusMessage; }
    public int getStatusCode() { return statusCode; }
    public String getUserMessage() { return userMessage; }
    public String getTechnicalMessage() { return technicalMessage; }
    public Map<String, String> getDetails() { return details; }
    public String getExecutionId() { return executionId; }
    public String getTimestamp() { return timestamp; }
    public String getPath() { return path; }

    // Builder
    public static Builder newBuilder() { return new Builder(); }

    public static class Builder {
        private final ErrorResponse err = new ErrorResponse();

        public Builder errorCode(String code) { err.errorCode = code; return this; }
        public Builder statusCode(int status) { err.statusCode = status; return this; }
        public Builder statusMessage(String message) { err.statusMessage = message; return this; }
        public Builder userMessage(String msg) { err.userMessage = msg; return this; }
        public Builder technicalMessage(String msg) { err.technicalMessage = msg; return this; }
        public Builder addDetail(String key, String value) { err.details.put(key, value); return this; }
        public Builder details(Map<String, String> map) { err.details.putAll(map); return this; }
        public Builder executionId(String id) { err.executionId = id; return this; }
        public Builder timestamp(String ts) { err.timestamp = ts; return this; }
        public Builder path(String path) { err.path = path; return this; }

        public ErrorResponse build() { return err; }
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "errorCode='" + errorCode + '\'' +
                ", statusCode=" + statusCode +
                ", statusMessage='" + statusMessage + '\'' +
                ", userMessage='" + userMessage + '\'' +
                ", technicalMessage='" + technicalMessage + '\'' +
                ", details=" + details +
                ", executionId='" + executionId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}

