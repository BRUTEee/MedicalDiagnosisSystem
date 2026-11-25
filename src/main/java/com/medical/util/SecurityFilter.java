package com.medical.util;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;

import com.medical.dao.DiagnosisDAO;
import com.medical.model.PatientPortalSession;
import com.medical.model.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Central access control for every request:
 *   - sets UTF-8 and basic security headers,
 *   - requires a signed-in user for everything except the public pages,
 *   - requires a valid CSRF token on every authenticated POST.
 */
@WebFilter("/*")
public class SecurityFilter extends HttpFilter {

    private static final long serialVersionUID = 1L;

    private static final Set<String> PUBLIC_PATHS =
            Set.of("/", "/index.jsp", "/login", "/patient-login");

    private static final Set<String> PATIENT_PORTAL_PATHS = Set.of(
            "/patient-dashboard", "/patient-logout");

    private static final String API_PATH = "/medical-assistant-chat";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain)
            throws IOException, ServletException {

        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding("UTF-8");
        }

        if (Boolean.parseBoolean(
                AppConfig.get("security.require-https", "false"))
                && !request.isSecure()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "A secure HTTPS connection is required.");
            return;
        }

        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "DENY");
        response.setHeader("Referrer-Policy", "same-origin");
        response.setHeader("Permissions-Policy",
                "camera=(), microphone=(), geolocation=()");

        byte[] nonceBytes = new byte[18];
        RANDOM.nextBytes(nonceBytes);
        String nonce = Base64.getEncoder().withoutPadding()
                .encodeToString(nonceBytes);
        request.setAttribute("cspNonce", nonce);

        String ollamaOrigin = "http://localhost:11434";

        try {
            ollamaOrigin = java.net.URI.create(
                    AppConfig.get("ollama.url", "http://localhost:11434/api/chat"))
                    .resolve("/").toString();
            ollamaOrigin = ollamaOrigin.endsWith("/")
                    ? ollamaOrigin.substring(0, ollamaOrigin.length() - 1)
                    : ollamaOrigin;
        } catch (IllegalArgumentException e) {
            ollamaOrigin = "'none'";
        }

        response.setHeader("Content-Security-Policy",
                "default-src 'self'; base-uri 'self'; object-src 'none'; "
                + "frame-ancestors 'none'; form-action 'self'; "
                + "script-src 'self' 'nonce-" + nonce + "'; "
                + "style-src 'self' 'unsafe-inline'; "
                + "connect-src 'self' " + ollamaOrigin);

        String path = request.getServletPath()
                + (request.getPathInfo() == null ? "" : request.getPathInfo());

        boolean publicPath = PUBLIC_PATHS.contains(path)
                || path.startsWith("/css/");

        if (publicPath) {
            if (("/login".equals(path) || "/patient-login".equals(path))
                    && "POST".equalsIgnoreCase(request.getMethod())
                    && !Csrf.isValid(request)) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Security check failed. Reload the page and try again.");
                return;
            }

            chain.doFilter(request, response);
            return;
        }
        HttpSession session = request.getSession(false);
        boolean patientPortalPath = PATIENT_PORTAL_PATHS.contains(path);
        boolean patientLoggedIn = session != null
                && session.getAttribute("patientPortal")
                        instanceof PatientPortalSession;

        if (patientPortalPath) {
            if (!patientLoggedIn) {
                response.sendRedirect(request.getContextPath() + "/patient-login");
                return;
            }

            response.setHeader("Cache-Control", "no-store");
            if ("POST".equalsIgnoreCase(request.getMethod())
                    && !Csrf.isValid(request)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "Security check failed. Go back, reload the page and try again.");
                return;
            }

            chain.doFilter(request, response);
            return;
        }

        if (patientLoggedIn) {
            PatientPortalSession portalSession = (PatientPortalSession) session.getAttribute("patientPortal");
            String servletPath = request.getServletPath();

            // Allow patient sessions to access their own appointments
            if ("/appointments".equals(servletPath)) {
                String patientIdParam = request.getParameter("patientId");
                if (patientIdParam != null && portalSession != null && patientIdParam.equals(portalSession.getPatientId())) {
                    chain.doFilter(request, response);
                    return;
                }
            }

            // Allow patient sessions to access diagnosis/assessment pages
            if ("/diagnosis".equals(servletPath)) {
                // For new assessment form with patientId
                String patientIdParam = request.getParameter("patientId");
                if (patientIdParam != null && portalSession != null && patientIdParam.equals(portalSession.getPatientId())) {
                    chain.doFilter(request, response);
                    return;
                }
                // For viewing existing assessment by ID
                String idParam = request.getParameter("id");
                if (idParam != null && portalSession != null) {
                    try {
                        int diagnosisId = Integer.parseInt(idParam);
                        DiagnosisDAO diagnosisDAO = new DiagnosisDAO();
                        com.medical.model.Diagnosis diagnosis = diagnosisDAO.getDiagnosisById(diagnosisId);
                        if (diagnosis != null && String.valueOf(diagnosis.getPatientId()).equals(portalSession.getPatientId())) {
                            chain.doFilter(request, response);
                            return;
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            response.sendError(HttpServletResponse.SC_FORBIDDEN,
                    "This page is only available to clinician accounts.");
            return;
        }

        boolean loggedIn = session != null
                && session.getAttribute("user") instanceof User;

        if (!loggedIn) {

            if (API_PATH.equals(path)) {
                writeJsonError(response,
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Your session has expired. Please sign in again.");
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
            }

            return;
        }

        // Authenticated pages contain patient data and must not be cached.
        response.setHeader("Cache-Control", "no-store");

        if ("POST".equalsIgnoreCase(request.getMethod())
                && !Csrf.isValid(request)) {

            if (API_PATH.equals(path)) {
                writeJsonError(response,
                        HttpServletResponse.SC_FORBIDDEN,
                        "Security check failed. Reload the page and try again.");
            } else {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Security check failed. Go back, reload the page and try again.");
            }

            return;
        }

        chain.doFilter(request, response);
    }

    private void writeJsonError(HttpServletResponse response,
                                int status, String message)
            throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(
                "{\"error\":\"" + message.replace("\"", "\\\"") + "\"}");
    }
}
