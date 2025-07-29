package com.t24.apiproxy.input.parsers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.t24.apiproxy.model.ApiRequest;

public class TextParser implements Parser {
    @Override
    public List<ApiRequest> parse(String path) throws Exception {
        List<ApiRequest> list = new ArrayList<>();
        for (String line : Files.readAllLines(Paths.get(path))) {
            if (line.isBlank()) continue;
            String[] parts = line.split(",", 5);
            ApiRequest req = new ApiRequest();
            // req.setUrl(parts[0]);
            // req.setMethod(parts[1]);
            // // req.setRawHeaders(parts[2]);
            // req.setBody(parts[3]);
            // req.setTimeout(Integer.parseInt(parts[4]));
            list.add(req);
        }
        return list;
    }
}
