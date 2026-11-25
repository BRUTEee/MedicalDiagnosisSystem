package com.medical.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Educational, rule-based keyword review of free-text symptoms.
 *
 * It only recognizes a fixed list of keywords. It does not diagnose, rank
 * likelihoods, or rule anything out, and its wording must stay that way.
 */
public class SymptomAnalyzer {

    private record Rule(String label, Pattern pattern, String note) {

        Rule(String label, String regex, String note) {
            this(label,
                 Pattern.compile("\\b(?:" + regex + ")\\b"),
                 note);
        }
    }

    private static final List<Rule> RULES = List.of(
        new Rule("Headache", "headaches?|migraines?",
            "Headaches can have many causes; this rule does not determine "
            + "the underlying cause."),
        new Rule("Fever", "fevers?|feverish|high temperature",
            "Clinical context and further assessment are needed to determine "
            + "its significance."),
        new Rule("Cough", "cough(?:s|ing)?",
            "This keyword match does not identify its cause."),
        new Rule("Fatigue", "fatigued?|tired(?:ness)?|exhaust(?:ed|ion)",
            "Fatigue can have many causes and requires assessment in the "
            + "context of the patient's history."),
        new Rule("Throat discomfort", "sore throat|throat pain",
            "This match does not establish a diagnosis."),
        new Rule("Nausea or vomiting", "nausea|nauseous|vomit(?:s|ing|ed)?",
            "Duration, hydration and associated symptoms should be reviewed."),
        new Rule("Diarrhea", "diarrh(?:o)?ea|loose stools?",
            "Duration, hydration and associated symptoms should be reviewed."),
        new Rule("Abdominal pain", "abdominal pain|stomach (?:pain|ache)|stomachache",
            "Location, severity and duration are needed to interpret it."),
        new Rule("Dizziness", "dizz(?:y|iness)|light-?headed(?:ness)?|vertigo",
            "Dizziness can have many causes and needs clinical assessment."),
        new Rule("Rash", "rash(?:es)?|hives",
            "Appearance, distribution and timing should be examined directly."),
        new Rule("Joint or muscle pain", "joint pain|muscle (?:pain|aches?)|body aches?",
            "This keyword match does not identify its cause."),
        new Rule("Runny or blocked nose", "runny nose|blocked nose|nasal congestion|sneezing",
            "This keyword match does not identify its cause.")
    );

    private static final List<Rule> RED_FLAGS = List.of(
        new Rule("Chest pain or pressure",
            "chest (?:pain|pressure|tightness)", ""),
        new Rule("Breathing difficulty",
            "shortness of breath|breathless(?:ness)?|"
            + "(?:difficulty|trouble) breathing|can(?:'|no)?t breathe", ""),
        new Rule("Loss of consciousness or fainting",
            "unconscious|lost consciousness|passed out|faint(?:ed|ing)?", ""),
        new Rule("Seizure", "seizures?|convulsions?", ""),
        new Rule("Possible stroke signs",
            "face droop(?:ing)?|slurred speech|"
            + "(?:weakness|numbness) on one side", ""),
        new Rule("Coughing or vomiting blood",
            "(?:coughing|vomiting) (?:up )?blood|blood in (?:stool|vomit|urine)", ""),
        new Rule("Confusion", "confus(?:ed|ion)|disoriented", ""),
        new Rule("Severe headache", "(?:severe|worst|sudden) headache", ""),
        new Rule("Thoughts of self-harm",
            "suicid(?:e|al)|self[- ]harm", "")
    );

    /**
     * Returns the labels of warning-sign keywords found in the text.
     * Keyword matching cannot understand negation or context, so a match
     * only means the text should be reviewed promptly by a clinician.
     */
    public List<String> findRedFlags(String symptoms) {

        List<String> flags = new ArrayList<>();

        if (symptoms == null || symptoms.isBlank()) {
            return flags;
        }

        String input = normalize(symptoms);

        for (Rule rule : RED_FLAGS) {
            if (rule.pattern().matcher(input).find()) {
                flags.add(rule.label());
            }
        }

        return flags;
    }

    public List<String> analyzeSymptoms(String symptoms) {

        List<String> results = new ArrayList<>();
        List<String> matchedSymptoms = new ArrayList<>();

        if (symptoms == null || symptoms.trim().isEmpty()) {
            results.add("No symptoms were provided for analysis.");
            return results;
        }

        String input = normalize(symptoms);

        List<String> redFlags = findRedFlags(symptoms);

        if (!redFlags.isEmpty()) {
            results.add(
                "WARNING-SIGN KEYWORDS FOUND: " + String.join(", ", redFlags)
                + ". These can be associated with serious conditions. "
                + "The patient should be assessed promptly by a qualified "
                + "clinician; arrange emergency care if the symptoms are "
                + "current or severe."
            );
        }

        for (Rule rule : RULES) {
            if (rule.pattern().matcher(input).find()) {
                matchedSymptoms.add(rule.label());
                results.add(rule.label() + " recognized. " + rule.note());
            }
        }

        if (matchedSymptoms.isEmpty()) {
            results.add(
                "No supported symptom keywords were recognized. " +
                "This does not rule out a medical condition."
            );
        } else {
            results.add(
                "Recognized symptom keywords: " +
                String.join(", ", matchedSymptoms) + "."
            );
        }

        results.add(
            "Safety note: This educational rule-based review is not a diagnosis. " +
            "Keyword matching cannot understand context or negation. " +
            "A qualified healthcare professional must assess the patient's symptoms."
        );

        return results;
    }

    private String normalize(String text) {
        return text.toLowerCase(Locale.ROOT)
                   .replace('\u2019', '\'')
                   .replaceAll("\\s+", " ")
                   .trim();
    }
}
