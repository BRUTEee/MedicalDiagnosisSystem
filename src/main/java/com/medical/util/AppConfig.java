package com.medical.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

/**
 * Application configuration.
 *
 * Lookup order for a key such as "db.password":
 *   1. environment variable  MDS_DB_PASSWORD
 *   2. JVM system property   db.password
 *   3. WEB-INF/app.properties (loaded at startup by AppContextListener)
 *   4. the supplied default
 */
public final class AppConfig {

    private static final Properties FILE_VALUES = new Properties();

    private AppConfig() {
    }

    public static synchronized void load(InputStream input) throws IOException {
        FILE_VALUES.clear();

        if (input != null) {
            FILE_VALUES.load(input);
        }
    }

    public static synchronized void clear() {
        FILE_VALUES.clear();
    }

    public static String get(String key, String defaultValue) {

        String value = System.getenv(environmentName(key));

        if (isBlank(value)) {
            value = System.getProperty(key);
        }

        if (isBlank(value)) {
            synchronized (AppConfig.class) {
                value = FILE_VALUES.getProperty(key);
            }
        }

        return isBlank(value) ? defaultValue : value.trim();
    }

    static String environmentName(String key) {
        return "MDS_" + key.toUpperCase(Locale.ROOT).replace('.', '_');
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
