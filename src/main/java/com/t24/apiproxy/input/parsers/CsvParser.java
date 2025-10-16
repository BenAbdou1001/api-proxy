package com.t24.apiproxy.input.parsers;

import java.io.FileReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;
import com.t24.apiproxy.model.ApiRequest;

public class CsvParser implements Parser {
    @Override
    public List<ApiRequest> parse(String path) throws Exception {
        List<ApiRequest> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(path))) {
            String[] line;
            reader.readNext(); // skip header
            while ((line = reader.readNext()) != null) {
                String url = line[0];
                String method = "null".equals(line[1]) ? null : line[1];
                String headers = "null".equals(line[2]) ? null : line[2];
                String body = "null".equals(line[3]) ? null : line[3];
                int timeout = Integer.parseInt(line[4]);

                // Prepare mutable headers map
                java.util.Map<String, String> headersMap = new java.util.HashMap<>();
                if (headers != null && !headers.isEmpty() && !headers.equals("null")) {
                    String[] headerPairs = headers.split(";");
                    for (String pair : headerPairs) {
                        String[] kv = pair.split(":", 2);
                        if (kv.length == 2) {
                            headersMap.put(kv[0].trim(), kv[1].trim());
                        }
                    }
                }

                ApiRequest.Builder builder = ApiRequest.newBuilder()
                    .url(URI.create(url).toURL())
                    .method(method)
                    .body("null".equals(body) ? null : body)
                    .timeout(timeout);
                    
                // Add headers to the request
                for (java.util.Map.Entry<String, String> entry : headersMap.entrySet()) {
                    builder.addHeader(entry.getKey(), entry.getValue());
                }

                ApiRequest request = builder.build();
                list.add(request);
            }
        }
        return list;
    }
}
