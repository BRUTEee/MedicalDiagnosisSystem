package com.medical.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import com.medical.dao.UserDAO;
import com.medical.util.LoginAttemptLimiter;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

class LoginServletTest {

    @Test
    void returnsTooManyRequestsWhenClientIsLockedOut() throws Exception {

        LoginAttemptLimiter limiter = new LoginAttemptLimiter(1, 60_000, 60_000, 10);
        limiter.recordFailure("192.0.2.1", System.currentTimeMillis());

        LoginServlet servlet = new LoginServlet();
        setField(servlet, "loginAttemptLimiter", limiter);
        CountingUserDAO userDAO = new CountingUserDAO();
        setField(servlet, "userDAO", userDAO);

        AtomicInteger status = new AtomicInteger();
        AtomicInteger retryAfter = new AtomicInteger();
        AtomicInteger forwards = new AtomicInteger();

        HttpServletRequest request = request(forwards);
        HttpServletResponse response = response(status, retryAfter);

        servlet.doPost(request, response);

        assertEquals(429, status.get());
        assertTrue(retryAfter.get() > 0);
        assertEquals(1, forwards.get());
        assertEquals(0, userDAO.calls);
    }

    private HttpServletRequest request(AtomicInteger forwards) {

        RequestDispatcher dispatcher = (RequestDispatcher) Proxy.newProxyInstance(
                RequestDispatcher.class.getClassLoader(),
                new Class<?>[] {RequestDispatcher.class},
                (proxy, method, args) -> {
                    if ("forward".equals(method.getName())) {
                        forwards.incrementAndGet();
                    }
                    return defaultValue(method.getReturnType());
                });

        InvocationHandler handler = (proxy, method, args) -> switch (
                method.getName()) {
            case "getCharacterEncoding" -> "UTF-8";
            case "getRemoteAddr" -> "192.0.2.1";
            case "getRequestDispatcher" -> dispatcher;
            case "getMethod" -> "POST";
            case "toString" -> "TestRequest";
            default -> defaultValue(method.getReturnType());
        };

        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[] {HttpServletRequest.class}, handler);
    }

    private HttpServletResponse response(AtomicInteger status,
                                         AtomicInteger retryAfter) {

        InvocationHandler handler = (proxy, method, args) -> {
            if ("setStatus".equals(method.getName())) {
                status.set((Integer) args[0]);
            } else if ("setHeader".equals(method.getName())
                    && "Retry-After".equals(args[0])) {
                retryAfter.set(Integer.parseInt((String) args[1]));
            }
            return defaultValue(method.getReturnType());
        };

        return (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class<?>[] {HttpServletResponse.class}, handler);
    }

    private void setField(Object target, String name, Object value)
            throws ReflectiveOperationException {
        Field field = LoginServlet.class.getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
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

    private static final class CountingUserDAO extends UserDAO {
        private int calls;

        @Override
        public com.medical.model.User authenticate(String email, String password) {
            calls++;
            return null;
        }
    }
}
