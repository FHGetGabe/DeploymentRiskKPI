package at.fhtw.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CredentialManager {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = CredentialManager.class.getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new IOException("config.properties not found in resources");
            }

            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    public static String getUsername() {
        return props.getProperty("username");
    }

    public static String getPassword() {
        return props.getProperty("password");
    }
}
