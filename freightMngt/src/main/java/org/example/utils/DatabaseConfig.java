package org.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();
    private static boolean isLoaded = false;

    static {
        try (InputStream input = DatabaseConfig.class
                .getClassLoader()
                .getResourceAsStream("database.properties")) {

            if (input == null) {
                System.err.println("⚠ database.properties not found - DB features disabled");
            } else {
                properties.load(input);
                isLoaded = true;
            }

        } catch (IOException ex) {
            System.err.println("⚠ Error loading database.properties: " + ex.getMessage());
        }
    }

    public static String getHostname() {
        return properties.getProperty("db.hostname", "localhost");
    }

    public static String getPort() {
        return properties.getProperty("db.port", "1521");
    }

    public static String getServiceName() {
        return properties.getProperty("db.servicename", "XEPDB1");
    }

    public static String getUsername() {
        return properties.getProperty("db.username", "freight_user");
    }

    public static String getPassword() {
        return properties.getProperty("db.password", "freight123");
    }

    public static boolean isConfigured() {
        return isLoaded && !properties.isEmpty();
    }
}