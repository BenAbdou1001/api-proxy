package com.t24.apiproxy.model;

import java.util.HashMap;
import java.util.Map;
public class ApiResponse {
    private int statusCode;
    private Map<String, String> headers = new HashMap<>();
    private Object body;
    private Metadata meta = new Metadata();

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Metadata getMeta() {
        return meta;
    }

    public void setMeta(Metadata meta) {
        this.meta = meta;
    }
    public static Builder newBuilder() {
        return new Builder();
    }
    public static class Builder {
        private final ApiResponse response = new ApiResponse();

        public Builder statusCode(int statusCode) {
            response.statusCode = statusCode;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            response.headers = headers;
            return this;
        }

        public Builder body(Object body) {
            response.body = body;
            return this;
        }

        public Builder meta(Metadata meta) {
            response.meta = meta;
            return this;
        }

        public ApiResponse build() {
            return response;
        }
    }
    @Override
    public String toString() {
        return "ApiResponse{" +
                "statusCode=" + statusCode +
                ", headers=" + headers +
                ", body=" + body +
                ", meta=" + meta +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ApiResponse that = (ApiResponse) o;
        return statusCode == that.statusCode &&
                headers.equals(that.headers) &&
                (body != null ? body.equals(that.body) : that.body == null) &&
                meta.equals(that.meta);
    }   
    @Override
    public int hashCode() {
        int result = statusCode;
        result = 31 * result + headers.hashCode();
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + meta.hashCode();
        return result;
    }   
    public static ApiResponse empty() {
        return new ApiResponse();
    }   
    public static ApiResponse of(int statusCode, Map<String, String> headers, Object body, Metadata meta) {
        return newBuilder()
                .statusCode(statusCode)
                .headers(headers)
                .body(body)
                .meta(meta)
                .build();
    }
    public static ApiResponse from(ApiResponse response) {
        if (response == null) {
            return empty();
        }
        return newBuilder()
                .statusCode(response.getStatusCode())
                .headers(response.getHeaders())
                .body(response.getBody())
                .meta(response.getMeta())
                .build();
    }
}
