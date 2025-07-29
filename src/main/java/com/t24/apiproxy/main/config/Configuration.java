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
}

