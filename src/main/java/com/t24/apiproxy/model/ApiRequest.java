package com.t24.apiproxy.model;

import java.util.Map;

public class ApiRequest {
    private String url;
    // private HttpMethod method;
    private Map<String, String> headers;
    private String body;
    private int timeout;
    private String contentType;
    private String acceptType;
    // Getters, setters, builders
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    // public HttpMethod getMethod() {
    //     return method;
    // }

    // public void setMethod(HttpMethod method) {
    //     this.method = method;
    // }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public int getTimeout() {
        return timeout;
    }
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public String getAcceptType() {
        return acceptType;
    }
    public void setAcceptType(String acceptType) {
        this.acceptType = acceptType;
    }
    
}
