package com.t24.apiproxy.input.parsers;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.t24.apiproxy.model.ApiRequest;

public class CsvParser implements Parser {
    @Override
    public List<ApiRequest> parse(String path) throws Exception {
        // Reading CSF file knowing its order 
        // A CSV file typically has only one request
        // each line represents a part of the request
        // e.g. url, method, headers, body, timeout
        List<ApiRequest> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] line;
            reader.readNext(); // skip header
            while ((line = reader.readNext()) != null) {
                
                
            }
        }
        return list;
    }
}
