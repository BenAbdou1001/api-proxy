package com.t24.apiproxy.main.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;

import com.t24.apiproxy.exception.InputProcessingException;
import com.t24.apiproxy.util.LoggingUtil;

/**
 * ConfigurationLoader is responsible for loading configuration from various sources.
 * 
 * Loading priority:
 * 1. System properties (highest priority)
 * 2. External file (if specified via system property 'config.file')
 * 3. Classpath resource (default)
 * 4. Environment variables (as fallback)
 * 
 * Usage examples:
 * - Load from classpath: ConfigurationLoader.load("application.properties")
 * - Load from file: ConfigurationLoader.loadFromFile("/path/to/config.properties")
 * - Load with defaults: ConfigurationLoader.loadWithDefaults("application.properties")
 */
public class ConfigurationLoader {
    
    private static final Logger logger = LoggingUtil.getLogger(ConfigurationLoader.class);
    private static final String CONFIG_FILE_PROPERTY = "config.file";
    
    /**
     * Loads configuration from the specified classpath resource.
     * Also checks for external file override via system property 'config.file'.
     * 
     * @param propFile The properties file name in the classpath
     * @return Configuration object
     * @throws InputProcessingException if configuration cannot be loaded
     */
    public static Configuration load(String propFile) {
        Properties props = new Properties();
        
        // 1. Try to load from external file if specified
        String externalFile = System.getProperty(CONFIG_FILE_PROPERTY);
        if (externalFile != null) {
            logger.info("Loading configuration from external file: {}", externalFile);
            try {
                props = loadFromFile(externalFile);
            } catch (InputProcessingException e) {
                logger.warn("Failed to load external config file, falling back to classpath: {}", e.getMessage());
            }
        }
        
        // 2. Load from classpath if not loaded from external file
        if (props.isEmpty()) {
            try (InputStream in = ConfigurationLoader.class
                    .getClassLoader()
                    .getResourceAsStream(propFile)) {
                if (in == null) {
                    throw new InputProcessingException("Missing config file: " + propFile);
                }
                props.load(in);
                logger.info("Loaded configuration from classpath: {}", propFile);
            } catch (IOException e) {
                throw new InputProcessingException("Error loading configuration: " + e.getMessage());
            }
        }
        
        // 3. Override with system properties
        Properties systemProps = System.getProperties();
        for (String key : systemProps.stringPropertyNames()) {
            if (key.startsWith("app.") || key.startsWith("http.") || 
                key.startsWith("ssl.") || key.startsWith("proxy.") || 
                key.startsWith("graphql.") || key.startsWith("logging.")) {
                props.setProperty(key, systemProps.getProperty(key));
            }
        }
        
        // 4. Override with environment variables (convert ENV_VAR to env.var)
        for (java.util.Map.Entry<String, String> entry : System.getenv().entrySet()) {
            String envKey = entry.getKey().toLowerCase().replace('_', '.');
            if (envKey.startsWith("app.") || envKey.startsWith("http.") || 
                envKey.startsWith("ssl.") || envKey.startsWith("proxy.") || 
                envKey.startsWith("graphql.") || envKey.startsWith("logging.")) {
                props.setProperty(envKey, entry.getValue());
            }
        }
        
        return new Configuration(props);
    }
    
    /**
     * Loads configuration from a file path.
     * 
     * @param filePath Absolute or relative path to the properties file
     * @return Properties loaded from the file
     * @throws InputProcessingException if the file cannot be read
     */
    public static Properties loadFromFile(String filePath) {
        Properties props = new Properties();
        File file = new File(filePath);
        
        if (!file.exists()) {
            throw new InputProcessingException("Configuration file not found: " + filePath);
        }
        
        if (!file.canRead()) {
            throw new InputProcessingException("Cannot read configuration file: " + filePath);
        }
        
        try (InputStream in = new FileInputStream(file)) {
            props.load(in);
            return props;
        } catch (IOException e) {
            throw new InputProcessingException("Error loading configuration from file: " + e.getMessage());
        }
    }
    
    /**
     * Loads configuration with default fallback values.
     * If the specified file is not found, returns a Configuration with default values.
     * 
     * @param propFile The properties file name in the classpath
     * @return Configuration object (with defaults if file not found)
     */
    public static Configuration loadWithDefaults(String propFile) {
        try {
            return load(propFile);
        } catch (InputProcessingException e) {
            logger.warn("Failed to load configuration, using defaults: {}", e.getMessage());
            return new Configuration(getDefaultProperties());
        }
    }
    
    /**
     * Creates a Configuration from a Properties object.
     * 
     * @param props Properties object
     * @return Configuration object
     */
    public static Configuration fromProperties(Properties props) {
        return new Configuration(props);
    }
    
    /**
     * Merges multiple properties files.
     * Later files override earlier ones.
     * 
     * @param propFiles Array of property file names
     * @return Merged Properties object
     */
    public static Properties mergeProperties(String... propFiles) {
        Properties merged = new Properties();
        
        for (String propFile : propFiles) {
            try (InputStream in = ConfigurationLoader.class
                    .getClassLoader()
                    .getResourceAsStream(propFile)) {
                if (in != null) {
                    Properties temp = new Properties();
                    temp.load(in);
                    merged.putAll(temp);
                    logger.info("Merged configuration from: {}", propFile);
                } else {
                    logger.warn("Property file not found, skipping: {}", propFile);
                }
            } catch (IOException e) {
                logger.warn("Error loading property file, skipping: {} - {}", propFile, e.getMessage());
            }
        }
        
        return merged;
    }
    
    /**
     * Gets default properties for the application.
     * 
     * @return Properties with default values
     */
    private static Properties getDefaultProperties() {
        Properties defaults = new Properties();
        
        // HTTP defaults
        defaults.setProperty("http.timeout", "30000");
        defaults.setProperty("http.connection.timeout", "10000");
        defaults.setProperty("http.read.timeout", "30000");
        defaults.setProperty("http.max.retry", "3");
        defaults.setProperty("http.retry.delay", "1000");
        
        // SSL defaults
        defaults.setProperty("ssl.verify", "true");
        
        // Logging defaults
        defaults.setProperty("logging.level", "INFO");
        defaults.setProperty("logging.file.path", "logs/apiproxy.log");
        
        // Security defaults
        defaults.setProperty("encryption.key", "default-encryption-key");
        defaults.setProperty("default.auth.timeout", "3600");
        
        // GraphQL defaults
        defaults.setProperty("graphql.endpoint", "http://localhost:4000/graphql");
        
        return defaults;
    }
    
    /**
     * Reloads configuration from the same source.
     * Useful for hot-reloading configuration changes.
     * 
     * @param propFile The properties file name
     * @return New Configuration object with reloaded values
     */
    public static Configuration reload(String propFile) {
        logger.info("Reloading configuration from: {}", propFile);
        return load(propFile);
    }
    
    /**
     * Validates that a configuration file exists and is readable.
     * 
     * @param propFile The properties file name in the classpath
     * @return true if the file exists and is readable
     */
    public static boolean validateConfigFile(String propFile) {
        try (InputStream in = ConfigurationLoader.class
                .getClassLoader()
                .getResourceAsStream(propFile)) {
            return in != null;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * Gets configuration information for debugging.
     * 
     * @param propFile The properties file name
     * @return String with configuration details
     */
    public static String getConfigInfo(String propFile) {
        StringBuilder info = new StringBuilder();
        info.append("Configuration Info:\n");
        info.append("  File: ").append(propFile).append("\n");
        info.append("  Exists: ").append(validateConfigFile(propFile)).append("\n");
        
        String externalFile = System.getProperty(CONFIG_FILE_PROPERTY);
        if (externalFile != null) {
            info.append("  External Override: ").append(externalFile).append("\n");
            info.append("  External Exists: ").append(new File(externalFile).exists()).append("\n");
        }
        
        return info.toString();
    }
}
