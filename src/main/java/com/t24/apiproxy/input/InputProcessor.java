package com.t24.apiproxy.input;

import java.util.ArrayList;
import java.util.List;

import com.t24.apiproxy.exception.ApiProxyException;
import com.t24.apiproxy.input.parsers.CsvParser;
import com.t24.apiproxy.input.parsers.JsonParser;
import com.t24.apiproxy.input.parsers.ParameterParser;
import com.t24.apiproxy.input.parsers.TextParser;
import com.t24.apiproxy.input.validation.InputValidator;
import com.t24.apiproxy.main.config.Configuration;
import com.t24.apiproxy.model.ApiRequest;

public class InputProcessor {
    public static List<ApiRequest> process(String[] args, Configuration cfg) {
        List<ApiRequest> all = new ArrayList<>();
        // decide format by extension or flag
        for (String pathOrParam : args) {
            try {
                if (pathOrParam.endsWith(".csv")) {
                    all.addAll(new CsvParser().parse(pathOrParam));
                } else if (pathOrParam.endsWith(".json")) {
                    all.addAll(new JsonParser().parse(pathOrParam));
                } else if (pathOrParam.endsWith(".txt")) {
                    all.addAll(new TextParser().parse(pathOrParam));
                } else {
                    all.addAll(new ParameterParser().parse(pathOrParam));
                }
            } catch (Exception e) {
                throw new ApiProxyException("INPUT_PROCESSING_ERROR",
                    "Failed to parse input: " + pathOrParam,
                    e.getMessage(), e);
            }
        }
        InputValidator.validate(all);
        return all;
    }
}
