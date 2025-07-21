package com.t24.apiproxy.main;

import com.t24.apiproxy.client.HttpClientManager;
import com.t24.apiproxy.config.Configuration;
import com.t24.apiproxy.config.ConfigurationLoader;
import com.t24.apiproxy.input.InputParser;
import com.t24.apiproxy.input.ParserFactory;
import com.t24.apiproxy.input.validation.RequestValidator;
import com.t24.apiproxy.model.ApiRequest;
import com.t24.apiproxy.model.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ApiProxyMain {
    private static final Logger logger = LoggerFactory.getLogger(ApiProxyMain.class);

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java -jar apiproxy.jar <input_type> <input_data>");
            System.exit(1);
        }

        String inputType = args[0];
        String inputData = args[1];

        try {
            // Load configuration
            Configuration config = ConfigurationLoader.load("application.properties");
            logger.info("Configuration loaded successfully");

            // Parse input
            InputParser parser = ParserFactory.getParser(inputType);
            List<ApiRequest> requests = parser.parse(inputData);
            logger.info("Parsed {} requests from input type: {}", requests.size(), inputType);

            // Validate requests
            for (ApiRequest request : requests) {
                RequestValidator.validate(request);
            }
            logger.info("All requests validated successfully");

            // Execute requests
            HttpClientManager clientManager = new HttpClientManager(config);
            List<ApiResponse> responses = clientManager.executeRequests(requests);
            logger.info("Executed {} requests", responses.size());

            // Output responses (for now, to console; extend as needed)
            for (ApiResponse response : responses) {
                System.out.println(response);
            }
        } catch (Exception e) {
            logger.error("Application failed", e);
            System.exit(1);
        }
    }
}