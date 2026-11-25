package com.medical.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.medical.model.PatientPortalSession;
import com.medical.util.AppConfig;
import com.medical.util.Csrf;
import com.medical.util.LoginAttemptLimiter;
import com.medical.util.PasswordUtil;

@WebServlet("/patient-login")
public class PatientPortalLoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String VIEW = "/WEB-INF/views/patient-login.jsp";
    private LoginAttemptLimiter loginAttemptLimiter;

    @Override
    public void init() {
        loginAttemptLimiter = new LoginAttemptLimiter(
                positiveIntConfig("login.max-failures", 5),
                positiveIntConfig("login.window-seconds", 900) * 1000L,
                positiveIntConfig("login.lock-seconds", 900) * 1000L,
                positiveIntConfig("login.max-clients", 10000));
    }

    private int positiveIntConfig(String key, int defaultValue) {
        String value = AppConfig.get(key, Integer.toString(defaultValue));
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        if (AppConfig.get("patient-demo.id", null) == null
                || !PasswordUtil.isHashed(
                        AppConfig.get("patient-demo.password-hash", null))) {
            request.setAttribute("configurationMessage",
                    "The demo patient portal has not been configured.");
        }
        Csrf.token(request.getSession(true));
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-store");
        String clientKey = request.getRemoteAddr();
        long now = System.currentTimeMillis();
        long lockedFor = loginAttemptLimiter.remainingLockMillis(clientKey, now);
        if (lockedFor > 0) {
            response.setHeader("Retry-After",
                    Long.toString(Math.max(1, (lockedFor + 999) / 1000)));
            response.setStatus(429);
            request.setAttribute("error",
                    "Too many sign-in attempts. Please wait before trying again.");
            request.getRequestDispatcher(VIEW).forward(request, response);
            return;
        }

        String expectedId = AppConfig.get("patient-demo.id", null);
        String expectedHash = AppConfig.get("patient-demo.password-hash", null);
        String patientId = request.getParameter("patientId");
        String password = request.getParameter("password");
        boolean valid = expectedId != null && expectedHash != null
                && patientId != null && password != null
                && PasswordUtil.isHashed(expectedHash)
                && expectedId.equals(patientId.trim())
                && PasswordUtil.verify(password, expectedHash);

        if (!valid) {
            loginAttemptLimiter.recordFailure(clientKey, System.currentTimeMillis());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("error", "Invalid patient ID or password.");
            request.setAttribute("patientId", patientId);
            request.getRequestDispatcher(VIEW).forward(request, response);
            return;
        }

        loginAttemptLimiter.recordSuccess(clientKey);
        HttpSession existing = request.getSession(false);
        if (existing != null) {
            existing.invalidate();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("patientPortal",
                new PatientPortalSession(expectedId,
                        AppConfig.get("patient-demo.name", "Demo patient")));
        Csrf.token(session);
        response.sendRedirect(request.getContextPath() + "/patient-dashboard");
    }
}