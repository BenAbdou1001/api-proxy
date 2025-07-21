package com.t24.apiproxy.model;

public class ApiResponse {
    private int statusCode;
    private String body;

    public ApiResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    // Getters
    public int getStatusCode() { return statusCode; }
    public String getBody() { return body; }
    // Setters
    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
    public void setBody(String body) { this.body = body; }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "statusCode=" + statusCode +
                ", body='" + body + '\'' +
                '}';
    }
}