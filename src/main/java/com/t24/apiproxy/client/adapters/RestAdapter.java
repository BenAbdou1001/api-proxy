package com.t24.apiproxy.client.adapters;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;

import com.t24.apiproxy.client.RequestBuilder;
import com.t24.apiproxy.client.ResponseProcessor;
import com.t24.apiproxy.main.config.Configuration;

public class RestAdapter {
    private final HttpClient httpClient;
    private final RequestBuilder builder;
    private final ResponseProcessor processor;

    public RestAdapter(Configuration cfg) {
        this.httpClient = HttpClients.custom().build();
        this.builder = new RequestBuilder();
        this.processor = new ResponseProcessor();
    }

    // public ApiResponse call(ApiRequest req) throws Exception {
    //     return processor.process(
    //         httpClient.execute(builder.build(req))
    //     );
    // }
}
