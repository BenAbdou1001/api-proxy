package com.t24.apiproxy.client;

import com.t24.apiproxy.client.adapters.GraphQLAdapter;
import com.t24.apiproxy.client.adapters.RestAdapter;
import com.t24.apiproxy.client.adapters.SoapAdapter;
import com.t24.apiproxy.main.config.Configuration;
import com.t24.apiproxy.model.ApiRequest;
import com.t24.apiproxy.model.ApiResponse;

public class HttpClientManager {
    private final RestAdapter restAdapter;
    private final SoapAdapter soapAdapter;
    private final GraphQLAdapter graphQLAdapter;

    public HttpClientManager(Configuration cfg) {
        this.restAdapter = new RestAdapter(cfg);
        this.soapAdapter = new SoapAdapter(cfg);
        this.graphQLAdapter = new GraphQLAdapter(cfg);
    }
    
    /**
     * Executes an API request based on its type
     * @param req The API request to execute
     * @return ApiResponse containing the result
     * @throws Exception if the request fails
     */
    public ApiResponse execute(ApiRequest req) throws Exception {
        if (req == null) {
            throw new IllegalArgumentException("ApiRequest cannot be null");
        }
        
        // Determine request type (default to REST if not specified)
        String requestType = req.getRequestType();
        if (requestType == null || requestType.isEmpty()) {
            requestType = "REST";
        }
        
        switch (requestType.toUpperCase()) {
            case "REST":
                return restAdapter.call(req);
                
            case "SOAP":
                return soapAdapter.call(req);
                
            case "GRAPHQL":
                return graphQLAdapter.call(req);
                
            default:
                throw new IllegalArgumentException("Unsupported API type: " + requestType + 
                    ". Supported types are: REST, SOAP, GRAPHQL");
        }
    }
}
