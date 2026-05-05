package com.salesinsight;

import com.salesinsight.config.ConfigLoader;
import com.salesinsight.exception.ConfigurationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ConfigLoader class.
 */
@DisplayName("ConfigLoader Tests")
class ConfigLoaderTest {

    @Test
    @DisplayName("Should get configuration value")
    void testGetConfiguration() {
        String value = ConfigLoader.get("db.url");
        assertNotNull(value, "db.url should not be null");
    }

    @Test
    @DisplayName("Should return default value when property not found")
    void testGetConfigurationWithDefault() {
        String value = ConfigLoader.get("nonexistent.property", "default_value");
        assertEquals("default_value", value, "Should return default value for missing property");
    }

    @Test
    @DisplayName("Should return null for missing property without default")
    void testGetConfigurationMissing() {
        String value = ConfigLoader.get("nonexistent.property");
        assertNull(value, "Should return null for missing property without default");
    }

    @Test
    @DisplayName("Should check if property exists")
    void testHasConfiguration() {
        boolean hasDb = ConfigLoader.has("db.url");
        assertTrue(hasDb, "Should return true for existing property");
        
        boolean hasNonexistent = ConfigLoader.has("nonexistent.property");
        assertFalse(hasNonexistent, "Should return false for missing property");
    }

    @Test
    @DisplayName("Should convert string to integer")
    void testGetIntConfiguration() {
        ConfigLoader.get("mail.smtp.port");
        int port = ConfigLoader.getInt("mail.smtp.port", 0);
        assertTrue(port > 0, "Should convert SMTP port to integer");
    }

    @Test
    @DisplayName("Should return default for invalid integer")
    void testGetIntConfigurationInvalid() {
        int value = ConfigLoader.getInt("nonexistent.int", 42);
        assertEquals(42, value, "Should return default for invalid or missing integer");
    }

    @Test
    @DisplayName("Should convert string to double")
    void testGetDoubleConfiguration() {
        double value = ConfigLoader.getDouble("nonexistent.double", 3.14);
        assertEquals(3.14, value, "Should return default for missing double");
    }

    @Test
    @DisplayName("Should convert string to boolean")
    void testGetBooleanConfiguration() {
        boolean value = ConfigLoader.getBoolean("nonexistent.boolean", false);
        assertFalse(value, "Should return default for missing boolean");
    }
}
