package com.t24.apiproxy.input.parsers;

import java.util.List;

public class CsvParser implements InputParser {
    public List<ApiRequest> parse(String csvData);
}
