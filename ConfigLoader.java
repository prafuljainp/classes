import java.io.InputStream;
import java.util.Properties;
/**
 * ConfigLoader is a utility class that loads configuration properties from a file named config.properties
 * located in the resources folder. It provides methods to retrieve property values by key.
 */
public class ConfigLoader {
    private static final Properties props = new Properties();
    // Load properties from config.properties file located in the resources folder
    static {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found in resources folder");
            }
            props.load(input);
        } catch (Exception e) {
            e.printStackTrace(); // Consider using a logger for production
        }
    }
    /*
     * Retrieves the value of a property by its key.
     * This method looks up the property in the loaded properties and returns its value.
     * The key of the property to retrieve.
     * The value of the property, or null if the key does not exist.
     */
    public static String get(String key) {
        return props.getProperty(key);
    }
    /*
     * Retrieves the value of a property by its key, with a default value if the key does not exist.
     * This method looks up the property in the loaded properties and returns its value,
     * or the provided default value if the key is not found.
     * The key of the property to retrieve.
     * The default value to return if the key does not exist.
     * The value of the property, or the default value if the key does not exist.
     */
    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }
    
}
