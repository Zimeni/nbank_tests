package org.example.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Config INSTANCE = new Config();
    private final Properties properties = new Properties();

    private Config() {
        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if(inputStream == null) {
                throw new RuntimeException("File config.properties is not found");
            }

            properties.load(inputStream);

        } catch (IOException exception) {
            throw new RuntimeException("Failed to load config.properties");
        }
    }

    public static String getProperty(String key) {
        return INSTANCE.properties.getProperty(key);
    }
}
