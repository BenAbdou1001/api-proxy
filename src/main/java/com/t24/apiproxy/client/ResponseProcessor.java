package com.t24.apiproxy.client;

import org.apache.http.HttpResponse;

import com.t24.apiproxy.model.ApiResponse;

public class ResponseProcessor {
    public ApiResponse process(HttpResponse httpResp) {
        // TODO: read status, body, headers
        // and map to ApiResponse
        assert httpResp != null : "HttpResponse cannot be null";
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatusCode(httpResp.getStatusLine().getStatusCode());
        // Additional processing can be done here, such as parsing JSON/XML, etc.
        return new ApiResponse();
    }
}
