package com.medical.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class SymptomAnalyzerTest {

    private final SymptomAnalyzer analyzer = new SymptomAnalyzer();

    @Test
    void recognizesKnownSymptoms() {

        List<String> review = analyzer.analyzeSymptoms(
                "The patient reports a headache and fever with a dry cough.");

        String text = String.join("\n", review);

        assertTrue(text.contains("Headache"));
        assertTrue(text.contains("Fever"));
        assertTrue(text.contains("Cough"));
    }

    @Test
    void reportsUnknownTextWithoutDiagnosing() {

        List<String> review = analyzer.analyzeSymptoms("xyzzy");

        String text = String.join("\n", review);

        assertTrue(text.contains("No supported symptom keywords"));

        // The wording must never claim a diagnosis.
        assertTrue(text.toLowerCase().contains("not a diagnosis"));
    }

    @Test
    void blankSymptomsProduceNoAnalysis() {

        assertEquals(List.of("No symptoms were provided for analysis."),
                analyzer.analyzeSymptoms("   "));
        assertEquals(List.of("No symptoms were provided for analysis."),
                analyzer.analyzeSymptoms(null));
    }

    @Test
    void alwaysAppendsSafetyNote() {

        List<String> review = analyzer.analyzeSymptoms("nausea");

        String last = review.get(review.size() - 1);

        assertTrue(last.startsWith("Safety note:"));
    }

    @Test
    void detectsWarningSigns() {

        List<String> flags = analyzer.findRedFlags(
                "Sudden chest pain with shortness of breath.");

        assertTrue(flags.contains("Chest pain or pressure"));
        assertTrue(flags.contains("Breathing difficulty"));
    }

    @Test
    void redFlagsAreReportedFirstInAnalysis() {

        List<String> review = analyzer.analyzeSymptoms(
                "Chest pain and a cough.");

        assertTrue(review.get(0).startsWith("WARNING-SIGN KEYWORDS FOUND:"));
    }

    @Test
    void detectsSelfHarmAndStrokeKeywords() {

        assertTrue(analyzer.findRedFlags("thoughts of self-harm").size() > 0);
        assertTrue(analyzer.findRedFlags("slurred speech").size() > 0);
    }

    @Test
    void emptyTextHasNoRedFlags() {

        assertTrue(analyzer.findRedFlags("").isEmpty());
        assertTrue(analyzer.findRedFlags(null).isEmpty());
        assertTrue(analyzer.findRedFlags("   ").isEmpty());
    }

    @Test
    void matchingIsCaseInsensitiveAndHandlesCurlyApostrophes() {

        assertFalse(analyzer.findRedFlags("CHEST PAIN").isEmpty());
        assertFalse(analyzer.findRedFlags("can\u2019t breathe").isEmpty());
    }

    @Test
    void wordBoundariesAvoidFalsePositives() {

        // "coughing" is a symptom; "coughdrop" is not a rule match.
        assertTrue(analyzer.findRedFlags("coughdrop").isEmpty());
    }
}
