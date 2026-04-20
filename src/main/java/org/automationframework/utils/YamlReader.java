package org.automationframework.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

/**
 * Utility class for reading YAML files.
 * Supports reading test data and configuration files in YAML format.
 */
public class YamlReader {

    private static final Logger logger = LoggerFactory.getLogger(YamlReader.class);
    private static final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    /**
     * Reads YAML file and returns as list of maps.
     *
     * @param filePath Path to YAML file
     * @return List of maps representing test data
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> readYaml(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.warn("YAML file not found: {}", filePath);
                return new ArrayList<>();
            }

            Object data = mapper.readValue(file, Object.class);

            if (data instanceof List) {
                logger.info("Successfully read {} test cases from YAML file", ((List<?>) data).size());
                return (List<Map<String, Object>>) data;
            } else if (data instanceof Map) {
                logger.info("Successfully read YAML file");
                return List.of((Map<String, Object>) data);
            }

            logger.warn("YAML file format not recognized");
            return new ArrayList<>();

        } catch (Exception e) {
            logger.error("Error reading YAML file: {}", filePath, e);
            throw new RuntimeException("Failed to read YAML file", e);
        }
    }

    /**
     * Reads specific key value from YAML file.
     *
     * @param filePath Path to YAML file
     * @param key Key to retrieve (supports dot notation for nested keys)
     * @return Value for the key
     */
    @SuppressWarnings("unchecked")
    public static Object readYamlKey(String filePath, String key) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.warn("YAML file not found: {}", filePath);
                return null;
            }

            Map<String, Object> data = mapper.readValue(file, Map.class);
            String[] keys = key.split("\\.");
            Object current = data;

            for (String k : keys) {
                if (current instanceof Map) {
                    current = ((Map<String, Object>) current).get(k);
                    if (current == null) {
                        logger.warn("Key not found: {}", key);
                        return null;
                    }
                } else {
                    logger.warn("Cannot traverse nested key: {}", key);
                    return null;
                }
            }

            logger.debug("Retrieved value for key '{}': {}", key, current);
            return current;

        } catch (Exception e) {
            logger.error("Error reading YAML file: {}", filePath, e);
            throw new RuntimeException("Failed to read YAML file", e);
        }
    }

    /**
     * Reads YAML file as a single Map object.
     *
     * @param filePath Path to YAML file
     * @return Map representation of YAML file
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> readYamlAsMap(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.warn("YAML file not found: {}", filePath);
                return new LinkedHashMap<>();
            }

            Object data = mapper.readValue(file, Object.class);

            if (data instanceof Map) {
                logger.info("Successfully read YAML file as Map");
                return (Map<String, Object>) data;
            }

            logger.warn("YAML file is not a Map structure");
            return new LinkedHashMap<>();

        } catch (Exception e) {
            logger.error("Error reading YAML file: {}", filePath, e);
            throw new RuntimeException("Failed to read YAML file", e);
        }
    }

    /**
     * Reads YAML file as a typed object.
     *
     * @param filePath Path to YAML file
     * @param targetClass Class to map YAML data to
     * @return Object of specified class
     */
    public static <T> T readYamlAsObject(String filePath, Class<T> targetClass) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.warn("YAML file not found: {}", filePath);
                return null;
            }

            T data = mapper.readValue(file, targetClass);
            logger.info("Successfully read YAML file as {}", targetClass.getSimpleName());
            return data;

        } catch (Exception e) {
            logger.error("Error reading YAML file: {}", filePath, e);
            throw new RuntimeException("Failed to read YAML file", e);
        }
    }

    /**
     * Writes data to YAML file.
     *
     * @param filePath Path to YAML file
     * @param data Data to write
     */
    public static void writeYaml(String filePath, Object data) {
        try {
            File file = new File(filePath);
            mapper.writeValue(file, data);
            logger.info("Successfully wrote data to YAML file: {}", filePath);
        } catch (Exception e) {
            logger.error("Error writing YAML file: {}", filePath, e);
            throw new RuntimeException("Failed to write YAML file", e);
        }
    }

    /**
     * Checks if YAML file exists.
     *
     * @param filePath Path to YAML file
     * @return true if file exists, false otherwise
     */
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }
}

