package com.medical.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

class AppConfigTest {

    @Test
    void clearRemovesLoadedConfiguration() throws Exception {

        try {
            AppConfig.load(new ByteArrayInputStream(
                    "test.setting=loaded".getBytes(StandardCharsets.UTF_8)));

            assertEquals("loaded", AppConfig.get("test.setting", "default"));

            AppConfig.clear();

            assertEquals("default", AppConfig.get("test.setting", "default"));

        } finally {
            AppConfig.clear();
        }
    }
}
