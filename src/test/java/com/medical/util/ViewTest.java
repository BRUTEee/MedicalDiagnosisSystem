package com.medical.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ViewTest {

    @Test
    void escapesHtmlSignificantCharacters() {

        assertEquals("&lt;script&gt;", View.esc("<script>"));
        assertEquals("&amp;", View.esc("&"));
        assertEquals("&quot;", View.esc("\""));
        assertEquals("&#x27;", View.esc("'"));
    }

    @Test
    void escapesAllFiveCharactersTogether() {

        assertEquals("&lt;a href=&quot;x&quot;&gt;&amp;&#x27;",
                View.esc("<a href=\"x\">&'"));
    }

    @Test
    void nullBecomesEmptyString() {
        assertEquals("", View.esc(null));
    }

    @Test
    void plainTextIsUnchanged() {
        assertEquals("Asha Perera", View.esc("Asha Perera"));
    }

    @Test
    void textShowsPlaceholderForBlankValues() {

        assertEquals("Not provided", View.text(null));
        assertEquals("Not provided", View.text(""));
        assertEquals("Not provided", View.text("   "));
        assertEquals("O+", View.text("O+"));
    }

    @Test
    void textEscapesNonBlankValues() {
        assertEquals("&lt;b&gt;", View.text("<b>"));
    }

    @Test
    void selectedMatchesCaseInsensitively() {

        assertEquals(" selected", View.selected("Male", "male"));
        assertEquals(" selected", View.selected("Male", "Male"));
        assertEquals("", View.selected("Male", "Female"));
        assertEquals("", View.selected("Male", null));
        assertEquals("", View.selected(null, "Male"));
    }

    @Test
    void dateHelpersHandleNull() {

        assertEquals("Not available", View.date(null));
        assertEquals("Not available", View.dateTime(null));
    }

    @Test
    void dateHelpersFormatTimestamps() {

        java.sql.Timestamp value =
                java.sql.Timestamp.valueOf("2026-03-04 15:07:00");

        assertEquals("04 Mar 2026", View.date(value));
        assertTrue(View.dateTime(value).startsWith("04 Mar 2026"));
        assertFalse(View.dateTime(value).isBlank());
    }
}
