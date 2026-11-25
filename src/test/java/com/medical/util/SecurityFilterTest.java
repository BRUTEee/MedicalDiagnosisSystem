package com.medical.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class SecurityFilterTest {

    private static final String REQUIRE_HTTPS = "security.require-https";

    private String previousRequireHttps;

    @AfterEach
    void restoreConfiguration() {

        if (previousRequireHttps == null) {
            System.clearProperty(REQUIRE_HTTPS);
        } else {
            System.setProperty(REQUIRE_HTTPS, previousRequireHttps);
        }
    }

    @Test
    void blocksInsecureRequestsWhenHttpsIsRequired() throws Exception {

        previousRequireHttps = System.getProperty(REQUIRE_HTTPS);
        System.setProperty(REQUIRE_HTTPS, "true");

        AtomicInteger status = new AtomicInteger();
        AtomicBoolean chainCalled = new AtomicBoolean();
        HttpServletRequest request = request(false);
        HttpServletResponse response = response(status);
        FilterChain chain = (req, res) -> chainCalled.set(true);

        new SecurityFilter().doFilter(request, response, chain);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, status.get());
        assertFalse(chainCalled.get());
    }

    @Test
    void allowsSecureRequestsWhenHttpsIsRequired() throws Exception {

        previousRequireHttps = System.getProperty(REQUIRE_HTTPS);
        System.setProperty(REQUIRE_HTTPS, "true");

        AtomicInteger status = new AtomicInteger();
        AtomicBoolean chainCalled = new AtomicBoolean();
        HttpServletRequest request = request(true);
        HttpServletResponse response = response(status);
        FilterChain chain = (req, res) -> chainCalled.set(true);

        new SecurityFilter().doFilter(request, response, chain);

        assertTrue(chainCalled.get());
        assertEquals(0, status.get());
    }

    @Test
    void patientPortalDashboardRequiresPatientSession() throws Exception {

        previousRequireHttps = System.getProperty(REQUIRE_HTTPS);
        System.clearProperty(REQUIRE_HTTPS);

        AtomicReference<String> redirect = new AtomicReference<>();
        AtomicBoolean chainCalled = new AtomicBoolean();
        HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[] {HttpServletRequest.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getCharacterEncoding" -> "UTF-8";
                    case "getServletPath" -> "/patient-dashboard";
                    case "getPathInfo" -> null;
                    case "getMethod" -> "GET";
                    case "getContextPath" -> "/MedicalDiagnosisSystem";
                    case "getSession" -> null;
                    default -> defaultValue(method.getReturnType());
                });
        HttpServletResponse response = (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class<?>[] {HttpServletResponse.class},
                (proxy, method, args) -> {
                    if ("sendRedirect".equals(method.getName())) {
                        redirect.set((String) args[0]);
                    }
                    return defaultValue(method.getReturnType());
                });

        new SecurityFilter().doFilter(request, response,
                (req, res) -> chainCalled.set(true));

        assertEquals("/MedicalDiagnosisSystem/patient-login", redirect.get());
        assertFalse(chainCalled.get());
    }

    @Test
    void allowsInsecureLocalRequestsWhenHttpsIsNotRequired() throws Exception {

        previousRequireHttps = System.getProperty(REQUIRE_HTTPS);
        System.clearProperty(REQUIRE_HTTPS);

        AtomicInteger status = new AtomicInteger();
        AtomicBoolean chainCalled = new AtomicBoolean();
        HttpServletRequest request = request(false);
        HttpServletResponse response = response(status);
        FilterChain chain = (req, res) -> chainCalled.set(true);

        new SecurityFilter().doFilter(request, response, chain);

        assertTrue(chainCalled.get());
        assertEquals(0, status.get());
    }

    private HttpServletRequest request(boolean secure) {

        InvocationHandler handler = (proxy, method, args) -> switch (
                method.getName()) {
            case "getCharacterEncoding" -> "UTF-8";
            case "isSecure" -> secure;
            case "getServletPath" -> "/";
            case "getPathInfo" -> null;
            case "getMethod" -> "GET";
            case "toString" -> "TestRequest";
            default -> defaultValue(method.getReturnType());
        };

        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[] {HttpServletRequest.class}, handler);
    }

    private HttpServletResponse response(AtomicInteger status) {

        InvocationHandler handler = (proxy, method, args) -> {
            if ("sendError".equals(method.getName())) {
                status.set((Integer) args[0]);
            }
            return defaultValue(method.getReturnType());
        };

        return (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class<?>[] {HttpServletResponse.class}, handler);
    }

    private Object defaultValue(Class<?> type) {

        if (!type.isPrimitive() || type == void.class) {
            return null;
        }
        if (type == boolean.class) {
            return false;
        }
        if (type == char.class) {
            return '\0';
        }
        return 0;
    }
}
