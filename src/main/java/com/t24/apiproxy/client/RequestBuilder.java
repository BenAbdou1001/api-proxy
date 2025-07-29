package com.t24.apiproxy.client;

import org.apache.http.client.methods.HttpRequestBase;

import com.t24.apiproxy.model.ApiRequest;

public class RequestBuilder {
    public HttpRequestBase build(ApiRequest req) {
        // TODO: map ApiRequest to HttpGet, HttpPost, etc.
        assert req != null : "ApiRequest cannot be null";
        if (req.getMethod() == null || req.getUrl() == null) {
            throw new IllegalArgumentException("ApiRequest method and URL must not be null");
        }

        // switch (req.getMethod().toUpperCase()) {
        //     case "GET":
        //         return new org.apache.http.client.methods.HttpGet(req.getUrl());
        //     case "POST":
        //         return new org.apache.http.client.methods.HttpPost(req.getUrl());
        //     case "PUT":
        //         return new org.apache.http.client.methods.HttpPut(req.getUrl());
        //     case "DELETE":
        //         return new org.apache.http.client.methods.HttpDelete(req.getUrl()); 
        //     case "PATCH":
        //         return new org.apache.http.client.methods.HttpPatch(req.getUrl());
        //     case "HEAD":
        //         return new org.apache.http.client.methods.HttpHead(req.getUrl());
        //     case "OPTIONS":
        //         return new org.apache.http.client.methods.HttpOptions(req.getUrl());
        //     case "TRACE":
        //         return new org.apache.http.client.methods.HttpTrace(req.getUrl());
        //     case "SOAP":
                // Handle SOAP request, possibly using a custom HttpRequestBase subclass
        //         throw new UnsupportedOperationException("SOAP method not implemented yet");
        //     case "GRAPHQL":
                // Handle GraphQL request, possibly using a custom HttpRequestBase subclass
        //         throw new UnsupportedOperationException("GraphQL method not implemented yet");
        //     default:    
        //         throw new IllegalArgumentException("Unsupported HTTP method: " + req.getMethod());
        // }
        return null;
    }
}
