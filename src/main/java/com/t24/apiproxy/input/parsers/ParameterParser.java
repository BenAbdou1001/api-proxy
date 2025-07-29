package com.t24.apiproxy.input.parsers;

import java.util.Collections;
import java.util.List;

import com.t24.apiproxy.model.ApiRequest;

public class ParameterParser implements Parser {
    public List<ApiRequest> parse(String arg) {
        ApiRequest req = new ApiRequest();
        // TODO: split and set fields
        return Collections.singletonList(req);
    }
}
