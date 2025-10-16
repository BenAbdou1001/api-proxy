package com.t24.apiproxy.main.config;

import java.util.Properties;

public class Configuration {
    private final Properties props;

    public Configuration(Properties props) {
        this.props = props;
    }

    public int getInt(String key, int defaultVal) {
        return Integer.parseInt(props.getProperty(key, String.valueOf(defaultVal)));
    }

    public String get(String key, String defaultVal) {
        return props.getProperty(key, defaultVal);
    }

    public boolean getBoolean(String key, boolean defaultVal) {
        return Boolean.parseBoolean(props.getProperty(key, String.valueOf(defaultVal)));
    }

    public long getLong(String key, long defaultVal) {
        return Long.parseLong(props.getProperty(key, String.valueOf(defaultVal)));
    }
    public double getDouble(String key, double defaultVal) {
        return Double.parseDouble(props.getProperty(key, String.valueOf(defaultVal)));
    }
    public Properties getProperties() {
        return props;
    }
    public String getRequired(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property '" + key + "' is missing");
        }
        return value;
    }
    public int getRequiredInt(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property '" + key + "' is missing");   
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Property '" + key + "' must be an integer", e);
        }
    }
    public boolean getRequiredBoolean(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property '" + key + "' is missing");
        }
        return Boolean.parseBoolean(value);
    }
    public long getRequiredLong(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property '" + key + "' is missing");
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Property '" + key + "' must be a long", e);
        }
    }
    public double getRequiredDouble(String key) {
        String value = props.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException("Required property '" + key + "' is missing");
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Property '" + key + "' must be a double", e);
        }
    }
    public String getRequiredOrDefault(String key, String defaultVal) {
        String value = props.getProperty(key);
        if (value == null) {
            if (defaultVal == null) {
                throw new IllegalArgumentException("Required property '" + key + "' is missing and no default value provided");
            }
            return defaultVal;
        }
        return value;
    }
    public int getRequiredOrDefaultInt(String key, int defaultVal) {
        String value = props.getProperty(key);
        if (value == null) {
            return defaultVal;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Property '" + key + "' must be an integer", e);
        }
    }
    public boolean getRequiredOrDefaultBoolean(String key, boolean defaultVal) {
        String value = props.getProperty(key);
        if (value == null) {
            return defaultVal;
        }
        return Boolean.parseBoolean(value);
    }
    public long getRequiredOrDefaultLong(String key, long defaultVal) {
        String value = props.getProperty(key);
        if (value == null) {
            return defaultVal;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Property '" + key + "' must be a long", e);
        }
    }
    public double getRequiredOrDefaultDouble(String key, double defaultVal) {
        String value = props.getProperty(key);
        if (value == null) {
            return defaultVal;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Property '" + key + "' must be a double", e);
        }
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Configuration{");
        for (String key : props.stringPropertyNames()) {            
            sb.append(key).append("=").append(props.getProperty(key)).append(", ");
        }
        if (sb.length() > 15) {     
            sb.setLength(sb.length() - 2); // Remove trailing comma and space
        }
        sb.append('}');
        return sb.toString();
    }

    /**
     * Gets the GraphQL endpoint URL from configuration.
     * 
     * @return The GraphQL endpoint URL
     * @throws IllegalArgumentException if the property is missing or invalid
     */
    public String getGraphQLEndpoint() {
        return getRequired("graphql.endpoint");
    }
    
    /**
     * Gets the HTTP client timeout configuration.
     * 
     * @return Timeout in milliseconds, default 30000 (30 seconds)
     */
    public int getHttpTimeout() {
        return getInt("http.timeout", 30000);
    }
    
    /**
     * Gets the HTTP connection timeout configuration.
     * 
     * @return Connection timeout in milliseconds, default 10000 (10 seconds)
     */
    public int getHttpConnectionTimeout() {
        return getInt("http.connection.timeout", 10000);
    }
    
    /**
     * Gets the HTTP read timeout configuration.
     * 
     * @return Read timeout in milliseconds, default 30000 (30 seconds)
     */
    public int getHttpReadTimeout() {
        return getInt("http.read.timeout", 30000);
    }
    
    /**
     * Gets the SSL verification setting.
     * 
     * @return true if SSL should be verified, default true
     */
    public boolean getSslVerify() {
        return getBoolean("ssl.verify", true);
    }
    
    /**
     * Gets the proxy host configuration.
     * 
     * @return Proxy host or null if not configured
     */
    public String getProxyHost() {
        return get("proxy.host", null);
    }
    
    /**
     * Gets the proxy port configuration.
     * 
     * @return Proxy port, default 8080
     */
    public int getProxyPort() {
        return getInt("proxy.port", 8080);
    }
    
    /**
     * Gets the proxy username configuration.
     * 
     * @return Proxy username or null if not configured
     */
    public String getProxyUsername() {
        return get("proxy.username", null);
    }
    
    /**
     * Gets the proxy password configuration.
     * 
     * @return Proxy password or null if not configured
     */
    public String getProxyPassword() {
        return get("proxy.password", null);
    }
    
    /**
     * Gets the logging level configuration.
     * 
     * @return Logging level (DEBUG, INFO, WARN, ERROR), default INFO
     */
    public String getLoggingLevel() {
        return get("logging.level", "INFO");
    }
    
    /**
     * Gets the logging file path configuration.
     * 
     * @return Logging file path or null if logging to console only
     */
    public String getLoggingFilePath() {
        return get("logging.file.path", null);
    }
    
    /**
     * Gets the maximum retry count configuration.
     * 
     * @return Maximum retry count, default 3
     */
    public int getMaxRetryCount() {
        return getInt("http.max.retry", 3);
    }
    
    /**
     * Gets the retry delay configuration.
     * 
     * @return Retry delay in milliseconds, default 1000 (1 second)
     */
    public int getRetryDelay() {
        return getInt("http.retry.delay", 1000);
    }
    
    /**
     * Checks if a property exists in the configuration.
     * 
     * @param key The property key
     * @return true if the property exists
     */
    public boolean hasProperty(String key) {
        return props.containsKey(key);
    }
    
    /**
     * Gets all property keys.
     * 
     * @return Set of all property keys
     */
    public java.util.Set<String> getPropertyKeys() {
        return props.stringPropertyNames();
    }
    
    /**
     * Validates that all required properties are present.
     * 
     * @param requiredKeys Array of required property keys
     * @throws IllegalArgumentException if any required property is missing
     */
    public void validateRequired(String... requiredKeys) {
        java.util.List<String> missing = new java.util.ArrayList<>();
        for (String key : requiredKeys) {
            if (!hasProperty(key)) {
                missing.add(key);
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Missing required configuration properties: " + missing);
        }
    }
}

