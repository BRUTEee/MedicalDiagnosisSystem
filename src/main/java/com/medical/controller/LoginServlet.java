package com.medical.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.medical.dao.DataAccessException;
import com.medical.dao.UserDAO;
import com.medical.model.User;
import com.medical.util.AppConfig;
import com.medical.util.Csrf;
import com.medical.util.LoginAttemptLimiter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String VIEW = "/WEB-INF/views/login.jsp";

    private UserDAO userDAO;
    private LoginAttemptLimiter loginAttemptLimiter;

    @Override
    public void init() {
        userDAO = new UserDAO();
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
            getServletContext().log("Invalid positive integer for " + key
                    + "; using the default.");
            return defaultValue;
        }
    }

    private String clientKey(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("user") instanceof User) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        response.setHeader("Cache-Control", "no-store");
        Csrf.token(request.getSession(true));
        request.getRequestDispatcher(VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setHeader("Cache-Control", "no-store");
        String clientKey = clientKey(request);
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

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        User user;

        try {
            user = userDAO.authenticate(email, password);

        } catch (DataAccessException e) {
            getServletContext().log("Sign-in failed: database error.", e);

            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute("error",
                "The system is temporarily unavailable. Please try again later.");
            request.getRequestDispatcher(VIEW).forward(request, response);
            return;
        }

        if (user == null) {
            loginAttemptLimiter.recordFailure(clientKey, System.currentTimeMillis());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            request.setAttribute("error", "Invalid email or password.");
            request.setAttribute("email", email);
            request.getRequestDispatcher(VIEW).forward(request, response);
            return;
        }

        loginAttemptLimiter.recordSuccess(clientKey);

        // Start from a clean session to prevent session fixation.
        HttpSession existing = request.getSession(false);

        if (existing != null) {
            existing.invalidate();
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        Csrf.token(session);

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
