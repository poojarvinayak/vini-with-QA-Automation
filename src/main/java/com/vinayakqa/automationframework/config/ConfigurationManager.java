package com.vinayakqa.automationframework.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuration Manager for loading and managing YAML configuration files.
 * Supports multiple environment configurations.
 */
public class ConfigurationManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    private static ConfigurationManager instance;
    private final Map<String, Object> configMap;

    private ConfigurationManager() {
        this.configMap = new LinkedHashMap<>();
        loadConfiguration();
    }

    /**
     * Gets singleton instance of ConfigurationManager.
     */
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * Loads configuration from YAML file.
     */
    private void loadConfiguration() {
        try {
            String configPath = System.getProperty("config.file", "src/main/resources/application.yaml");
            File configFile = new File(configPath);

            if (!configFile.exists()) {
                logger.warn("Configuration file not found at: {}", configPath);
                logger.info("Using default configuration values");
                return;
            }

            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> yamlConfig = mapper.readValue(configFile, Map.class);
            configMap.putAll(yamlConfig);

            logger.info("Configuration loaded successfully from: {}", configPath);
        } catch (Exception e) {
            logger.error("Error loading configuration file", e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    /**
     * Gets a configuration value by key with dot notation (e.g., "app.baseUrl").
     */
    public String getString(String key) {
        return getString(key, "");
    }

    /**
     * Gets a configuration value by key with default value.
     */
    public String getString(String key, String defaultValue) {
        try {
            Object value = getNestedValue(key);
            return value != null ? String.valueOf(value) : defaultValue;
        } catch (Exception e) {
            logger.warn("Error retrieving string value for key: {}", key);
            return defaultValue;
        }
    }

    /**
     * Gets an integer configuration value.
     */
    public int getInt(String key, int defaultValue) {
        try {
            Object value = getNestedValue(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            logger.warn("Error retrieving int value for key: {}", key);
            return defaultValue;
        }
    }

    /**
     * Gets a boolean configuration value.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        try {
            Object value = getNestedValue(key);
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            return Boolean.parseBoolean(String.valueOf(value));
        } catch (Exception e) {
            logger.warn("Error retrieving boolean value for key: {}", key);
            return defaultValue;
        }
    }

    /**
     * Gets nested value using dot notation.
     */
    @SuppressWarnings("unchecked")
    private Object getNestedValue(String key) {
        String[] keys = key.split("\\.");
        Object current = configMap;

        for (String k : keys) {
            if (current instanceof Map) {
                current = ((Map<String, Object>) current).get(k);
                if (current == null) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return current;
    }

    /**
     * Gets the entire configuration map.
     */
    public Map<String, Object> getConfigMap() {
        return configMap;
    }

    /**
     * Resets configuration (useful for testing).
     */
    public void reset() {
        configMap.clear();
        loadConfiguration();
    }
}
