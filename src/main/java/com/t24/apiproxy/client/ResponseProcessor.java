package com.t24.apiproxy.client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.t24.apiproxy.model.ApiResponse;

public class ResponseProcessor {
    public ApiResponse process(HttpResponse httpResp) {
        assert httpResp != null : "HttpResponse cannot be null";
        long receivedAt = System.currentTimeMillis();

        int statusCode = httpResp.getStatusLine().getStatusCode();
        String statusMessage = httpResp.getStatusLine().getReasonPhrase();

        // Headers
        Map<String, String> headers = new HashMap<>();
        for (Header header : httpResp.getAllHeaders()) {
            headers.put(header.getName(), header.getValue());
        }

        // Cookies
        StringJoiner cookieJoiner = new StringJoiner(";");
        for (Header header : httpResp.getHeaders("Set-Cookie")) {
            cookieJoiner.add(header.getValue());
        }
        String cookies = cookieJoiner.length() > 0 ? cookieJoiner.toString() : null;

        // Body, Content-Type, Content-Length
        String body = null;
        String contentType = null;
        long contentLength = -1;
        try {
            HttpEntity entity = httpResp.getEntity();
            if (entity != null) {
                body = EntityUtils.toString(entity);
                contentType = entity.getContentType() != null ? entity.getContentType().getValue() : null;
                contentLength = entity.getContentLength();
            }
        } catch (IOException e) {
            return ApiResponse.newBuilder()
                    .statusCode(statusCode)
                    .statusMessage(statusMessage)
                    .headers(headers)
                    .cookies(cookies)
                    .errorMessage(e.getMessage())
                    .success(false)
                    .receivedAt(receivedAt)
                    .build();
        }

        return ApiResponse.newBuilder()
                .statusCode(statusCode)
                .statusMessage(statusMessage)
                .headers(headers)
                .cookies(cookies)
                .body(body)
                .contentType(contentType)
                .contentLength(contentLength)
                .success(statusCode >= 200 && statusCode < 300)
                .receivedAt(receivedAt)
                .build();
    }
}
