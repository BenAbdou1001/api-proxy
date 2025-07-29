package com.t24.apiproxy.input.parsers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t24.apiproxy.model.ApiRequest;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JsonParser implements Parser {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<ApiRequest> parse(String path) throws Exception {
        byte[] json = Files.readAllBytes(Paths.get(path));
        return mapper.readValue(json, new TypeReference<List<ApiRequest>>() {});
    }
}
