package com.t24.apiproxy.input.parsers;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t24.apiproxy.model.ApiRequest;

public class JsonParser implements Parser {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<ApiRequest> parse(String path) throws Exception {
        byte[] json = Files.readAllBytes(Paths.get(path));
        List<ApiRequestDto> dtos = mapper.readValue(json, new TypeReference<List<ApiRequestDto>>() {});
        return dtos.stream().map(JsonParser::toApiRequest).collect(Collectors.toList());
    }

    // DTO class for JSON mapping
    private static class ApiRequestDto {
        public String name;
        public String method;
        public String url;
        public Map<String, String> queryParams;
        public Object body;
        public String formData;
        public String multipartData;
        public Map<String, String> headers;
        public String cookies;
        public String authType;
        public String authUsername;
        public String authPassword;
        public Integer timeout;
        public Integer connectionTimeout;
        public Integer readTimeout;
        public Boolean followRedirects;
        public Integer maxRedirects;
        public Boolean verifySsl;
        public String proxyHost;
        public Integer proxyPort;
        public String proxyUsername;
        public String proxyPassword;
        public String expectedStatus;
        public String validateResponse;
        public String outputFile;
        public Integer retryCount;
        public Integer retryDelay;
        public String requestType;
    }

    // Map DTO to ApiRequest using the builder
    private static ApiRequest toApiRequest(ApiRequestDto dto) {
        ApiRequest.Builder builder = ApiRequest.newBuilder();

        if (dto.name != null) builder.name(dto.name);
        if (dto.method != null) builder.method(dto.method);
        if (dto.url != null) {
            try { builder.url(new URL(dto.url)); } catch (Exception ignored) {}
        }
        if (dto.queryParams != null) builder.queryParams(dto.queryParams);
        if (dto.body != null) builder.body(dto.body);
        if (dto.formData != null) builder.formData(dto.formData);
        if (dto.multipartData != null) builder.multipartData(dto.multipartData);
        if (dto.headers != null) builder.queryParams(dto.headers);
        if (dto.cookies != null) builder.cookies(dto.cookies);
        if (dto.authType != null) builder.authType(dto.authType);
        if (dto.authUsername != null) builder.authUsername(dto.authUsername);
        if (dto.authPassword != null) builder.authPassword(dto.authPassword);
        if (dto.timeout != null) builder.timeout(dto.timeout);
        if (dto.connectionTimeout != null) builder.connectionTimeout(dto.connectionTimeout);
        if (dto.readTimeout != null) builder.readTimeout(dto.readTimeout);
        if (dto.followRedirects != null) builder.followRedirects(dto.followRedirects);
        if (dto.maxRedirects != null) builder.maxRedirects(dto.maxRedirects);
        if (dto.verifySsl != null) builder.verifySsl(dto.verifySsl);
        if (dto.proxyHost != null) builder.proxyHost(dto.proxyHost);
        if (dto.proxyPort != null) builder.proxyPort(dto.proxyPort);
        if (dto.proxyUsername != null) builder.proxyUsername(dto.proxyUsername);
        if (dto.proxyPassword != null) builder.proxyPassword(dto.proxyPassword);
        if (dto.expectedStatus != null) builder.expectedStatus(dto.expectedStatus);
        if (dto.validateResponse != null) builder.validateResponse(dto.validateResponse);
        if (dto.outputFile != null) builder.outputFile(dto.outputFile);
        if (dto.retryCount != null) builder.retryCount(dto.retryCount);
        if (dto.retryDelay != null) builder.retryDelay(dto.retryDelay);
        if (dto.requestType != null) builder.requestType(dto.requestType);

        return builder.build();
    }
}
