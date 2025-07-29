package com.t24.apiproxy.model;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ApiRequest {
    private String method;
    private URL url;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> queryParams = new HashMap<>();
    private Object body;
    private Metadata meta = new Metadata();

    public ApiRequest() {}

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getMethod() {
        return method;
    }

    public URL getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Object getBody() {
        return body;
    }

    public Metadata getMeta() {
        return meta;
    }

    public static class Builder {
        private final ApiRequest req = new ApiRequest();

        public Builder method(String method) {
            req.method = method;
            return this;
        }

        public Builder url(URL url) {
            req.url = url;
            return this;
        }

        public Builder addHeader(String name, String value) {
            req.headers.put(name, value);
            return this;
        }

        public Builder addQueryParam(String name, String value) {
            req.queryParams.put(name, value);
            return this;
        }

        public Builder body(Object body) {
            req.body = body;
            return this;
        }

        public Builder meta(String key, Object value) {
            req.meta.add(key, value);
            return this;
        }

        public ApiRequest build() {
            return req;
        }
    }
}