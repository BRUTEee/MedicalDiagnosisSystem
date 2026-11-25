package com.medical.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.ServletContext;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.ServletContextEvent;

class AppContextListenerTest {

    private static final String REQUIRE_HTTPS = "security.require-https";

    private String previousRequireHttps;

    @AfterEach
    void restoreConfiguration() {

        AppConfig.clear();
        if (previousRequireHttps == null) {
            System.clearProperty(REQUIRE_HTTPS);
        } else {
            System.setProperty(REQUIRE_HTTPS, previousRequireHttps);
        }
    }

    @Test
    void enablesSecureSessionCookieWhenHttpsIsRequired() {

        previousRequireHttps = System.getProperty(REQUIRE_HTTPS);
        System.setProperty(REQUIRE_HTTPS, "true");
        AtomicBoolean secure = new AtomicBoolean();
        Map<String, String> attributes = new HashMap<>();

        initialize(secure, attributes);

        assertTrue(secure.get());
        assertEquals("Lax", attributes.get("SameSite"));
    }

    @Test
    void leavesSecureCookieOffForLocalHttpByDefault() {

        previousRequireHttps = System.getProperty(REQUIRE_HTTPS);
        System.clearProperty(REQUIRE_HTTPS);
        AtomicBoolean secure = new AtomicBoolean(true);
        Map<String, String> attributes = new HashMap<>();

        initialize(secure, attributes);

        assertFalse(secure.get());
        assertEquals("Lax", attributes.get("SameSite"));
    }

    private void initialize(AtomicBoolean secure,
                            Map<String, String> attributes) {

        byte[] config = new byte[0];
        SessionCookieConfig cookieConfig = (SessionCookieConfig)
                Proxy.newProxyInstance(
                        SessionCookieConfig.class.getClassLoader(),
                        new Class<?>[] {SessionCookieConfig.class},
                        (proxy, method, args) -> {
                            if ("setSecure".equals(method.getName())) {
                                secure.set((Boolean) args[0]);
                            }
                            if ("setAttribute".equals(method.getName())) {
                                attributes.put((String) args[0], (String) args[1]);
                            }
                            return null;
                        });
        ServletContext context = (ServletContext) Proxy.newProxyInstance(
                ServletContext.class.getClassLoader(),
                new Class<?>[] {ServletContext.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getResourceAsStream" -> new ByteArrayInputStream(config);
                    case "getSessionCookieConfig" -> cookieConfig;
                    default -> null;
                });

        new AppContextListener().contextInitialized(
                new ServletContextEvent(context));
    }
}
