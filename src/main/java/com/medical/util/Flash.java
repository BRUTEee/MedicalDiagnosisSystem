package com.medical.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** One-time messages shown on the page after a redirect. */
public final class Flash {

    private static final String MESSAGE_KEY = "flashMessage";
    private static final String TYPE_KEY = "flashType";

    private Flash() {
    }

    public static void success(HttpServletRequest request, String message) {
        set(request, "success", message);
    }

    public static void error(HttpServletRequest request, String message) {
        set(request, "error", message);
    }

    private static void set(HttpServletRequest request,
                            String type, String message) {

        HttpSession session = request.getSession();
        session.setAttribute(MESSAGE_KEY, message);
        session.setAttribute(TYPE_KEY, type);
    }

    /** Returns {type, message} and clears it, or null when there is none. */
    public static String[] consume(HttpSession session) {

        if (session == null) {
            return null;
        }

        Object message = session.getAttribute(MESSAGE_KEY);
        Object type = session.getAttribute(TYPE_KEY);

        if (message == null) {
            return null;
        }

        session.removeAttribute(MESSAGE_KEY);
        session.removeAttribute(TYPE_KEY);

        return new String[] {
            "error".equals(type) ? "error" : "success",
            message.toString()
        };
    }
}
