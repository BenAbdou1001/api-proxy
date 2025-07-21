package com.t24.apiproxy.input;

import java.util.List;
import java.util.Map;
import com.t24.apiproxy.model.ApiRequest;

public class InputProcessor {
    private Map<String, InputParser> parsers;
    
    public List<ApiRequest> processInput(String inputType, String inputData);
    public boolean validateInputFormat(String inputType, String inputData);
}
