package com.medical.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ReportServletCsvTest {

    @Test
    void valuesAreQuoted() {
        assertEquals("\"Asha Perera\"", ReportServlet.csv("Asha Perera"));
    }

    @Test
    void embeddedQuotesAreDoubled() {

        assertEquals("\"He said \"\"hello\"\"\"",
                ReportServlet.csv("He said \"hello\""));
    }

    @Test
    void nullBecomesEmptyQuotedField() {
        assertEquals("\"\"", ReportServlet.csv(null));
    }

    @Test
    void commaIsSafeBecauseFieldIsQuoted() {

        String field = ReportServlet.csv("fever, cough");

        assertEquals("\"fever, cough\"", field);
    }

    @Test
    void newlinesArePreservedInsideQuotes() {

        String field = ReportServlet.csv("line one\nline two");

        assertTrue(field.startsWith("\""));
        assertTrue(field.endsWith("\""));
        assertTrue(field.contains("\n"));
    }

    @Test
    void formulaCharactersAreNeutralized() {

        assertEquals("\"'=1+1\"", ReportServlet.csv("=1+1"));
        assertEquals("\"'+1\"", ReportServlet.csv("+1"));
        assertEquals("\"'-1\"", ReportServlet.csv("-1"));
        assertEquals("\"'@cmd\"", ReportServlet.csv("@cmd"));

        assertFalse(ReportServlet.csv("=1+1").contains("\"=\""));
        assertTrue(ReportServlet.csv("=1+1").contains("\"'="));
    }

    @Test
    void tabAndCarriageReturnAreNeutralized() {

        assertEquals("\"'\tdata\"", ReportServlet.csv("\tdata"));
        assertEquals("\"'\rdata\"", ReportServlet.csv("\rdata"));
    }

    @Test
    void ordinaryTextIsNotPrefixed() {

        assertFalse(ReportServlet.csv("fever").contains("'"));
        assertTrue(ReportServlet.csv("fever").equals("\"fever\""));
    }
}
