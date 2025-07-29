package com.t24.apiproxy.input.parsers;

import java.util.List;

import com.t24.apiproxy.model.ApiRequest;

public interface Parser {
    List<ApiRequest> parse(String path) throws Exception;
}
