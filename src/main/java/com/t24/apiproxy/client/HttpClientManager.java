package com.t24.apiproxy.client;

import com.t24.apiproxy.client.adapters.GraphQLAdapter;
import com.t24.apiproxy.client.adapters.RestAdapter;
import com.t24.apiproxy.client.adapters.SoapAdapter;
import com.t24.apiproxy.main.config.Configuration;

public class HttpClientManager {
    private final Configuration cfg;
    private final RestAdapter restAdapter;
    private final SoapAdapter soapAdapter;
    private final GraphQLAdapter gqlAdapter;

    public HttpClientManager(Configuration cfg) {
        this.cfg = cfg;
        this.restAdapter = new RestAdapter(cfg);
        this.soapAdapter = new SoapAdapter(cfg);
        this.gqlAdapter = new GraphQLAdapter(cfg);
    }

    // public ApiResponse execute(ApiRequest req) {
    //     try {
    //         switch (req.getMethod().toUpperCase()) {
    //             case "GET":
    //                 return restAdapter.call(req);
    //             case "POST":
    //                 return restAdapter.call(req);
    //             case "PUT":
    //                 return restAdapter.call(req);
    //             case "DELETE":
    //                 return restAdapter.call(req);
    //             case "SOAP":
    //                 return soapAdapter.call(req);
    //             case "GRAPHQL":
    //                 return gqlAdapter.call(req);
    //             case "PATCH":
    //                 return restAdapter.call(req); // Assuming PATCH is handled by REST adapter
    //             case "HEAD":
    //                 return restAdapter.call(req); // Assuming HEAD is handled by REST adapter
    //             case "OPTIONS":
    //                 return restAdapter.call(req); // Assuming OPTIONS is handled by REST adapter
    //             case "TRACE":
    //                 return restAdapter.call(req); // Assuming TRACE is handled by REST adapter
    //             case "CONNECT":
    //                 return restAdapter.call(req); // Assuming CONNECT is handled by REST adapter
    //             default:
    //                 throw new NetworkException("UNSUPPORTED_PROTOCOL",
    //                     "Method not supported: " + req.getMethod(),
    //                     null);
    //         }
    //     } catch (Exception e) {
    //         throw new NetworkException("HTTP_ERROR",
    //             "Error calling " + req.getUrl(),
    //             e.getMessage(), e);
    //     }
    }
