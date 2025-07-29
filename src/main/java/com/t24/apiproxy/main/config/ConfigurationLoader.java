package com.t24.apiproxy.main.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.t24.apiproxy.exception.InputProcessingException;

public class ConfigurationLoader {
    public static Configuration load(String propFile) {
        Properties props = new Properties();
        try (InputStream in = 
                ConfigurationLoader.class
                    .getClassLoader()
                    .getResourceAsStream(propFile)) {
            if (in == null) {
                throw new InputProcessingException("Missing config file: " + propFile);
            }
            props.load(in);
            return new Configuration(props);
        } catch (IOException e) {
            throw new InputProcessingException("Error loading configuration: " + e.getMessage());
        }
    }
}
