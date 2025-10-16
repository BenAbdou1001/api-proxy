package com.t24.apiproxy.input;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.t24.apiproxy.exception.ApiProxyException;
import com.t24.apiproxy.exception.InputProcessingException;
import com.t24.apiproxy.input.parsers.CsvParser;
import com.t24.apiproxy.input.parsers.JsonParser;
import com.t24.apiproxy.input.parsers.TextParser;
import com.t24.apiproxy.input.validation.InputValidator;
import com.t24.apiproxy.main.config.Configuration;
import com.t24.apiproxy.model.ApiRequest;

public class InputProcessor {
    public static List<ApiRequest> process(String[] args, Configuration cfg) {
        if (args == null || args.length == 0) {
            throw new ApiProxyException("INPUT_PROCESSING_ERROR", "No input provided", null, null);
        }

        List<ApiRequest> all = new ArrayList<>();
        String first = args[0];

        try {
            String lowerFirst = first.toLowerCase();
            if (lowerFirst.endsWith(".csv") || lowerFirst.endsWith(".json") || lowerFirst.endsWith(".txt")) {
                // Check if file exists
                File file = new File(first);
                if (!file.exists()) {
                    throw new InputProcessingException("Input file does not exist: " + first);
                }
                if (!file.canRead()) {
                    throw new InputProcessingException("Cannot read input file: " + first);
                }
                
                // Parse based on file type
                if (lowerFirst.endsWith(".csv")) {
                    all.addAll(new CsvParser().parse(first));
                } else if (lowerFirst.endsWith(".json")) {
                    all.addAll(new JsonParser().parse(first));
                } else if (lowerFirst.endsWith(".txt")) {
                    all.addAll(new TextParser().parse(first));
                }
            }
            // URL input: treat first as URL, rest as parameters
            else {
                ApiRequest.Builder builder;
                try {
                    builder = ApiRequest.newBuilder().url(java.net.URI.create(first).toURL());
                } catch (MalformedURLException e) {
                    throw new InputProcessingException("Invalid URL: " + first + " - " + e.getMessage());
                } catch (IllegalArgumentException e) {
                    throw new InputProcessingException("Invalid URI syntax: " + first + " - " + e.getMessage());
                }
                
                for (int i = 1; i < args.length; i++) {
                    String param = args[i];
                    
                    try {
                        // Example: method=POST, header:Authorization=Bearer, etc.
                        if (param.startsWith("method=")) {
                            builder.method(param.substring(7));
                        } else if (param.startsWith("name=")) {
                            builder.name(param.substring(5));
                        } else if (param.startsWith("header:")) {
                            String[] kv = param.substring(7).split("=", 2);
                            if (kv.length == 2) {
                                builder.addHeader(kv[0], kv[1]);
                            } else {
                                throw new InputProcessingException("Invalid header format: " + param + ". Expected format: header:Key=Value");
                            }
                        } else if (param.startsWith("query:")) {
                            String[] kv = param.substring(6).split("=", 2);
                            if (kv.length == 2) {
                                builder.addQueryParam(kv[0], kv[1]);
                            } else {
                                throw new InputProcessingException("Invalid query parameter format: " + param + ". Expected format: query:key=value");
                            }
                        } else if (param.startsWith("body=")) {
                            builder.body(param.substring(5));
                        } else if (param.startsWith("formData=")) {
                            builder.formData(param.substring(9));
                        } else if (param.startsWith("multipartData=")) {
                            builder.multipartData(param.substring(14));
                        } else if (param.startsWith("cookies=")) {
                            builder.cookies(param.substring(8));
                        } else if (param.startsWith("authType=")) {
                            builder.authType(param.substring(9));
                        } else if (param.startsWith("authUsername=")) {
                            builder.authUsername(param.substring(13));
                        } else if (param.startsWith("authPassword=")) {
                            builder.authPassword(param.substring(13));
                        } else if (param.startsWith("timeout=")) {
                            try {
                                builder.timeout(Integer.parseInt(param.substring(8)));
                            } catch (NumberFormatException e) {
                                throw new InputProcessingException("Invalid timeout value: " + param.substring(8) + ". Must be an integer.");
                            }
                        } else if (param.startsWith("connectionTimeout=")) {
                            try {
                                builder.connectionTimeout(Integer.parseInt(param.substring(18)));
                            } catch (NumberFormatException e) {
                                throw new InputProcessingException("Invalid connectionTimeout value: " + param.substring(18) + ". Must be an integer.");
                            }
                        } else if (param.startsWith("readTimeout=")) {
                            try {
                                builder.readTimeout(Integer.parseInt(param.substring(12)));
                            } catch (NumberFormatException e) {
                                throw new InputProcessingException("Invalid readTimeout value: " + param.substring(12) + ". Must be an integer.");
                            }
                        } else if (param.startsWith("followRedirects=")) {
                            builder.followRedirects(Boolean.parseBoolean(param.substring(16)));
                        } else if (param.startsWith("maxRedirects=")) {
                            try {
                                builder.maxRedirects(Integer.parseInt(param.substring(13)));
                            } catch (NumberFormatException e) {
                                throw new InputProcessingException("Invalid maxRedirects value: " + param.substring(13) + ". Must be an integer.");
                            }
                        } else if (param.startsWith("verifySsl=")) {
                            builder.verifySsl(Boolean.parseBoolean(param.substring(10)));
                        } else if (param.startsWith("proxyHost=")) {
                            builder.proxyHost(param.substring(10));
                        } else if (param.startsWith("proxyPort=")) {
                            try {
                                builder.proxyPort(Integer.parseInt(param.substring(10)));
                            } catch (NumberFormatException e) {
                                throw new InputProcessingException("Invalid proxyPort value: " + param.substring(10) + ". Must be an integer.");
                            }
                        } else if (param.startsWith("proxyUsername=")) {
                            builder.proxyUsername(param.substring(14));
                        } else if (param.startsWith("proxyPassword=")) {
                            builder.proxyPassword(param.substring(14));
                        } else if (param.startsWith("expectedStatus=")) {
                            builder.expectedStatus(param.substring(15));
                        } else if (param.startsWith("validateResponse=")) {
                            builder.validateResponse(param.substring(17));
                        } else if (param.startsWith("outputFile=")) {
                            builder.outputFile(param.substring(11));
                        } else if (param.startsWith("retryCount=")) {
                            try {
                                builder.retryCount(Integer.parseInt(param.substring(11)));
                            } catch (NumberFormatException e) {
                                throw new InputProcessingException("Invalid retryCount value: " + param.substring(11) + ". Must be an integer.");
                            }
                        } else if (param.startsWith("retryDelay=")) {
                            try {
                                builder.retryDelay(Integer.parseInt(param.substring(11)));
                            } catch (NumberFormatException e) {
                                throw new InputProcessingException("Invalid retryDelay value: " + param.substring(11) + ". Must be an integer.");
                            }
                        } else if (param.startsWith("requestType=")) {
                            builder.requestType(param.substring(12));
                        } else {
                            // Log warning for unknown parameter
                            System.err.println("WARNING: Unknown parameter ignored: " + param);
                        }
                    } catch (InputProcessingException e) {
                        throw e; // Re-throw InputProcessingException as-is
                    } catch (Exception e) {
                        throw new InputProcessingException("Error processing parameter '" + param + "': " + e.getMessage());
                    }
                }
                
                ApiRequest req = builder.build();
                if (req.getMethod() == null) {
                    throw new ApiProxyException("INPUT_PROCESSING_ERROR", "Missing required parameter: method", null, null);
                }
                all.add(req);
            }
        } catch (InputProcessingException e) {
            throw e; // Re-throw InputProcessingException as-is
        } catch (ApiProxyException e) {
            throw e; // Re-throw ApiProxyException as-is
        } catch (Exception e) {
            throw new InputProcessingException("Failed to parse input '" + first + "': " + e.getMessage());
        }

        // Validate all parsed requests
        InputValidator.validate(all);
        return all;
    }
}
