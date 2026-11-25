package com.medical.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/** Helpers used by the JSP views. */
public final class View {

    private View() {
    }

    /** HTML-escapes a value; null becomes an empty string. */
    public static String esc(Object value) {

        if (value == null) {
            return "";
        }

        String text = value.toString();
        StringBuilder out = new StringBuilder(text.length() + 16);

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            switch (c) {
                case '&' -> out.append("&amp;");
                case '<' -> out.append("&lt;");
                case '>' -> out.append("&gt;");
                case '"' -> out.append("&quot;");
                case '\'' -> out.append("&#x27;");
                default -> out.append(c);
            }
        }

        return out.toString();
    }

    /** HTML-escapes a value, showing a placeholder when it is blank. */
    public static String text(String value) {

        if (value == null || value.isBlank()) {
            return "Not provided";
        }

        return esc(value);
    }

    public static String dateTime(Date value) {

        if (value == null) {
            return "Not available";
        }

        return new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(value);
    }

    public static String date(Date value) {

        if (value == null) {
            return "Not available";
        }

        return new SimpleDateFormat("dd MMM yyyy").format(value);
    }

    /** Returns " selected" when the two values match (for option tags). */
    public static String selected(String optionValue, String currentValue) {
        return optionValue != null && optionValue.equalsIgnoreCase(
                currentValue == null ? "" : currentValue) ? " selected" : "";
    }
}
