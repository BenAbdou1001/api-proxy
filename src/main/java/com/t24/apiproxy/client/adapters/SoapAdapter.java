package com.t24.apiproxy.client.adapters;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.t24.apiproxy.client.ResponseProcessor;
import com.t24.apiproxy.exception.NetworkException;
import com.t24.apiproxy.main.config.Configuration;
import com.t24.apiproxy.model.ApiRequest;
import com.t24.apiproxy.model.ApiResponse;

public class SoapAdapter {
    private final ResponseProcessor responseProcessor;
    
    public SoapAdapter(Configuration cfg) {
        this.responseProcessor = new ResponseProcessor();
    }

    /**
     * Executes a SOAP API call
     * @param req The API request to execute
     * @return ApiResponse containing the result
     * @throws Exception if the request fails
     */
    public ApiResponse call(ApiRequest req) throws Exception {
        if (req == null) {
            throw new IllegalArgumentException("ApiRequest cannot be null");
        }
        
        long startTime = System.currentTimeMillis();
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            
            // SOAP always uses POST
            HttpPost httpPost = new HttpPost(req.getUrl().toString());
            
            // Set SOAP headers
            httpPost.setHeader("Content-Type", "text/xml; charset=utf-8");
            httpPost.setHeader("SOAPAction", getSoapAction(req));
            
            // Add custom headers if any
            if (req.getHeaders() != null) {
                for (String key : req.getHeaders().keySet()) {
                    httpPost.setHeader(key, req.getHeaders().get(key));
                }
            }
            
            // Wrap body in SOAP envelope if needed
            String soapBody = wrapInSoapEnvelope(req);
            HttpEntity entity = new StringEntity(soapBody, ContentType.TEXT_XML);
            httpPost.setEntity(entity);
            
            // Execute request
            try (CloseableHttpResponse httpResponse = httpClient.execute(httpPost)) {
                ApiResponse response = responseProcessor.process(httpResponse);
                
                // Calculate response time
                long responseTime = System.currentTimeMillis() - startTime;
                
                // Unwrap SOAP response
                Object unwrappedBody = unwrapSoapResponse(response.getBody());
                
                return ApiResponse.newBuilder()
                    .statusCode(response.getStatusCode())
                    .statusMessage(response.getStatusMessage())
                    .headers(response.getHeaders())
                    .cookies(response.getCookies())
                    .body(unwrappedBody)
                    .contentType(response.getContentType())
                    .contentLength(response.getContentLength())
                    .responseTime(responseTime)
                    .receivedAt(response.getReceivedAt())
                    .errorMessage(response.getErrorMessage())
                    .success(response.isSuccess())
                    .build();
            }
            
        } catch (IOException e) {
            throw new NetworkException("NETWORK_ERROR", 
                "Failed to execute SOAP request", 
                e.getMessage(), e);
        }
    }
    
    /**
     * Gets the SOAPAction header value
     */
    private String getSoapAction(ApiRequest req) {
        // Check if SOAPAction is in headers
        if (req.getHeaders() != null && req.getHeaders().containsKey("SOAPAction")) {
            return req.getHeaders().get("SOAPAction");
        }
        // Default empty SOAPAction
        return "";
    }
    
    /**
     * Wraps the request body in a SOAP envelope if not already wrapped
     */
    private String wrapInSoapEnvelope(ApiRequest req) {
        if (req.getBody() == null) {
            throw new IllegalArgumentException("SOAP request body cannot be null");
        }
        
        String body = req.getBody().toString().trim();
        
        // Check if already wrapped in SOAP envelope
        if (body.contains("<soap:Envelope") || body.contains("<SOAP-ENV:Envelope") || 
            body.contains("<soapenv:Envelope") || body.contains("<env:Envelope")) {
            return body;
        }
        
        // Wrap in SOAP 1.1 envelope
        StringBuilder soapEnvelope = new StringBuilder();
        soapEnvelope.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        soapEnvelope.append("<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
        soapEnvelope.append("  <soap:Body>\n");
        soapEnvelope.append("    ").append(body).append("\n");
        soapEnvelope.append("  </soap:Body>\n");
        soapEnvelope.append("</soap:Envelope>");
        
        return soapEnvelope.toString();
    }
    
    /**
     * Unwraps the SOAP response to extract the body content
     */
    private Object unwrapSoapResponse(Object responseBody) {
        if (responseBody == null) {
            return null;
        }
        
        String body = responseBody.toString().trim();
        
        // Check if it's a SOAP response
        if (!body.contains("<soap:") && !body.contains("<SOAP-ENV:") && 
            !body.contains("<soapenv:") && !body.contains("<env:")) {
            return body; // Not a SOAP response, return as-is
        }
        
        // Try to extract the Body content
        String[] bodyTags = {
            "<soap:Body>", "</soap:Body>",
            "<SOAP-ENV:Body>", "</SOAP-ENV:Body>",
            "<soapenv:Body>", "</soapenv:Body>",
            "<env:Body>", "</env:Body>"
        };
        
        for (int i = 0; i < bodyTags.length; i += 2) {
            String openTag = bodyTags[i];
            String closeTag = bodyTags[i + 1];
            
            int startIdx = body.indexOf(openTag);
            int endIdx = body.indexOf(closeTag);
            
            if (startIdx != -1 && endIdx != -1) {
                String extracted = body.substring(startIdx + openTag.length(), endIdx).trim();
                return extracted.isEmpty() ? body : extracted;
            }
        }
        
        // If we can't extract, return the whole body
        return body;
    }
}
