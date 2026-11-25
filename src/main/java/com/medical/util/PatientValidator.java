package com.medical.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.medical.model.Patient;

/** Server-side validation of patient details (limits match the schema). */
public final class PatientValidator {

    public static final List<String> GENDERS =
            List.of("Male", "Female", "Other");

    public static final List<String> BLOOD_GROUPS =
            List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

    private static final Pattern PHONE =
            Pattern.compile("[0-9+()\\-\\s]{5,20}");

    private static final Pattern EMAIL =
            Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    private PatientValidator() {
    }

    /** Trims text fields and converts blank optional fields to null. */
    public static void normalize(Patient patient) {
        patient.setName(trim(patient.getName()));
        patient.setGender(trim(patient.getGender()));
        patient.setBloodGroup(trim(patient.getBloodGroup()));
        patient.setPhone(trim(patient.getPhone()));
        patient.setEmail(trim(patient.getEmail()));
        patient.setAddress(trim(patient.getAddress()));
    }

    /** Returns the problems found; an empty list means the patient is valid. */
    public static List<String> validate(Patient patient) {

        List<String> errors = new ArrayList<>();

        if (patient.getName() == null) {
            errors.add("Full name is required.");
        } else if (patient.getName().length() > 100) {
            errors.add("Full name must be 100 characters or fewer.");
        }

        if (patient.getAge() < 1 || patient.getAge() > 120) {
            errors.add("Age must be between 1 and 120.");
        }

        if (patient.getGender() != null
                && !GENDERS.contains(patient.getGender())) {
            errors.add("Select a valid gender.");
        }

        if (patient.getBloodGroup() != null
                && !BLOOD_GROUPS.contains(patient.getBloodGroup())) {
            errors.add("Select a valid blood group.");
        }

        if (patient.getPhone() != null
                && !PHONE.matcher(patient.getPhone()).matches()) {
            errors.add("Phone must be 5-20 characters: digits, spaces, + - ( ).");
        }

        if (patient.getEmail() != null
                && (patient.getEmail().length() > 100
                    || !EMAIL.matcher(patient.getEmail()).matches())) {
            errors.add("Enter a valid email address (100 characters or fewer).");
        }

        if (patient.getAddress() != null
                && patient.getAddress().length() > 255) {
            errors.add("Address must be 255 characters or fewer.");
        }

        return errors;
    }

    private static String trim(String value) {

        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
