package com.t24.apiproxy.main;

import java.util.List;

import com.t24.apiproxy.client.HttpClientManager;
import com.t24.apiproxy.exception.ApiProxyException;
import com.t24.apiproxy.input.InputProcessor;
import com.t24.apiproxy.main.config.Configuration;
import com.t24.apiproxy.main.config.ConfigurationLoader;
import com.t24.apiproxy.model.ApiRequest;
import com.t24.apiproxy.util.LoggingUtil;

public class ApiProxyMain {
    public static void main(String[] args) {
        try {
            LoggingUtil.init();
            Configuration cfg = ConfigurationLoader.load("application.properties");
            List<ApiRequest> requests = InputProcessor.process(args, cfg);
            HttpClientManager clientMgr = new HttpClientManager(cfg);
            // for (ApiRequest req : requests) {
            //     ApiResponse resp = clientMgr.execute(req);
            //     System.out.println(resp);
            // }
        } catch (ApiProxyException e) {
            System.err.println("Fatal error: " + e.getUserMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
