package com.t24.apiproxy.client.adapters;

import com.t24.apiproxy.main.config.Configuration;
import com.t24.apiproxy.model.ApiRequest;
import com.t24.apiproxy.model.ApiResponse;

public class SoapAdapter {
    public SoapAdapter(Configuration cfg) {
        // TODO: init SOAP client
    }

    public ApiResponse call(ApiRequest req) {
        // TODO: wrap/unwrap SOAP envelope
        return new ApiResponse();
    }
}
