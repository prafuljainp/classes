package com.salesinsight.config;

import com.salesinsight.exception.ConfigurationException;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

/**
 * ConfigLoader is a utility class that loads configuration from multiple sources:
 * 1. Environment variables
 * 2. .env file
 * 3. config.properties file in resources
 * 
 * Properties are loaded in the order above, with later sources overriding earlier ones.
 */
public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final Properties props = new Properties();
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static final String[] REQUIRED_PROPERTIES = {
        "db.url", "db.username", "db.password",
        "mail.smtp.host", "mail.smtp.port", "mail.smtp.username", "mail.smtp.password",
        "mail.recipient", "gemini.api.url", "gemini.api.key"
    };

    static {
        try {
            loadProperties();
            validateRequiredProperties();
            logger.info("Configuration loaded successfully");
        } catch (Exception e) {
            logger.error("Failed to load configuration", e);
            throw new ConfigurationException("Failed to load configuration: " + e.getMessage(), e);
        }
    }

    /**
     * Loads properties from config.properties file in resources.
     */
    private static void loadProperties() {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input != null) {
                props.load(input);
                logger.debug("Loaded properties from config.properties");
            } else {
                logger.warn("config.properties not found, using .env or environment variables");
            }
        } catch (Exception e) {
            logger.error("Error loading config.properties", e);
            throw new ConfigurationException("Error loading config.properties: " + e.getMessage(), e);
        }
    }

    /**
     * Validates that all required properties are set.
     */
    private static void validateRequiredProperties() {
        StringBuilder missingProps = new StringBuilder();
        for (String prop : REQUIRED_PROPERTIES) {
            if (get(prop) == null) {
                missingProps.append(prop).append(", ");
            }
        }
        
        if (missingProps.length() > 0) {
            String missing = missingProps.substring(0, missingProps.length() - 2);
            throw new ConfigurationException("Missing required properties: " + missing);
        }
    }

    /**
     * Retrieves a configuration value from multiple sources.
     * Priority: System property > Environment variable > .env file > config.properties > default value
     *
     * @param key The property key
     * @return The property value, or null if not found
     */
    public static String get(String key) {
        return get(key, null);
    }

    /**
     * Retrieves a configuration value with a default fallback.
     *
     * @param key          The property key
     * @param defaultValue The default value if key is not found
     * @return The property value, or defaultValue if not found
     */
    public static String get(String key, String defaultValue) {
        // 1. Check system properties
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }

        // 2. Check environment variables (replace dots with underscores and uppercase)
        String envKey = key.toUpperCase().replace(".", "_");
        value = System.getenv(envKey);
        if (value != null) {
            return value;
        }

        // 3. Check .env file
        value = dotenv.get(envKey);
        if (value != null) {
            return value;
        }

        // 4. Check config.properties
        value = props.getProperty(key);
        if (value != null) {
            return value;
        }

        // 5. Return default value
        return defaultValue;
    }

    /**
     * Checks if a property exists.
     *
     * @param key The property key
     * @return true if the property exists, false otherwise
     */
    public static boolean has(String key) {
        return get(key) != null;
    }

    /**
     * Gets an integer configuration value.
     *
     * @param key          The property key
     * @param defaultValue The default value if parsing fails or key not found
     * @return The integer value or defaultValue
     */
    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid integer value for property '{}': {}", key, value);
            }
        }
        return defaultValue;
    }

    /**
     * Gets a double configuration value.
     *
     * @param key          The property key
     * @param defaultValue The default value if parsing fails or key not found
     * @return The double value or defaultValue
     */
    public static double getDouble(String key, double defaultValue) {
        String value = get(key);
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                logger.warn("Invalid double value for property '{}': {}", key, value);
            }
        }
        return defaultValue;
    }

    /**
     * Gets a boolean configuration value.
     *
     * @param key          The property key
     * @param defaultValue The default value if parsing fails or key not found
     * @return The boolean value or defaultValue
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value != null) {
            return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes") || value.equals("1");
        }
        return defaultValue;
    }
}
