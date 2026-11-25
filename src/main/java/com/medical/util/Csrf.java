package com.medical.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Per-session CSRF token (synchronizer token pattern). */
public final class Csrf {

    public static final String PARAMETER = "csrfToken";
    public static final String HEADER = "X-CSRF-Token";

    private static final String SESSION_KEY = "csrfToken";
    private static final SecureRandom RANDOM = new SecureRandom();

    private Csrf() {
    }

    /** Returns the session's token, creating one if necessary. */
    public static String token(HttpSession session) {

        synchronized (session) {
            String token = (String) session.getAttribute(SESSION_KEY);

            if (token == null) {
                byte[] bytes = new byte[32];
                RANDOM.nextBytes(bytes);

                token = Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(bytes);

                session.setAttribute(SESSION_KEY, token);
            }

            return token;
        }
    }

    /** Hidden form field carrying the token. */
    public static String field(HttpServletRequest request) {
        return "<input type=\"hidden\" name=\"" + PARAMETER + "\" value=\""
                + View.esc(token(request.getSession())) + "\">";
    }

    /** Checks the token sent as a form parameter or request header. */
    public static boolean isValid(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return false;
        }

        String expected = (String) session.getAttribute(SESSION_KEY);

        String supplied = request.getHeader(HEADER);

        if (supplied == null) {
            supplied = request.getParameter(PARAMETER);
        }

        if (expected == null || supplied == null) {
            return false;
        }

        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8));
    }
}
