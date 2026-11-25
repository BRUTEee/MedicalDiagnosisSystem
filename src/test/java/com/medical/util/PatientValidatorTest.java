package com.medical.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.medical.model.Patient;

class PatientValidatorTest {

    private static Patient validPatient() {

        Patient patient = new Patient();

        patient.setName("Asha Perera");
        patient.setAge(34);
        patient.setGender("Female");
        patient.setBloodGroup("O+");
        patient.setPhone("+94 77 123 4567");
        patient.setEmail("asha@example.com");
        patient.setAddress("12 Galle Road, Colombo");

        return patient;
    }

    @Test
    void completePatientIsValid() {
        assertTrue(PatientValidator.validate(validPatient()).isEmpty());
    }

    @Test
    void optionalFieldsMayBeMissing() {

        Patient patient = new Patient();
        patient.setName("Nimal");
        patient.setAge(5);

        assertTrue(PatientValidator.validate(patient).isEmpty());
    }

    @Test
    void nameIsRequired() {

        Patient patient = validPatient();
        patient.setName(null);

        assertTrue(PatientValidator.validate(patient)
                .contains("Full name is required."));
    }

    @Test
    void nameLengthIsLimited() {

        Patient patient = validPatient();
        patient.setName("a".repeat(101));

        assertTrue(PatientValidator.validate(patient)
                .contains("Full name must be 100 characters or fewer."));
    }

    @Test
    void ageMustBeInRange() {

        Patient patient = validPatient();

        patient.setAge(0);
        assertTrue(PatientValidator.validate(patient)
                .contains("Age must be between 1 and 120."));

        patient.setAge(121);
        assertTrue(PatientValidator.validate(patient)
                .contains("Age must be between 1 and 120."));
    }

    @Test
    void genderAndBloodGroupMustBeFromTheAllowedLists() {

        Patient patient = validPatient();

        patient.setGender("Unknown");
        assertTrue(PatientValidator.validate(patient)
                .contains("Select a valid gender."));

        patient.setGender("Male");
        patient.setBloodGroup("C");
        assertTrue(PatientValidator.validate(patient)
                .contains("Select a valid blood group."));
    }

    @Test
    void phoneFormatIsChecked() {

        Patient patient = validPatient();
        patient.setPhone("call me");

        assertTrue(PatientValidator.validate(patient)
                .contains("Phone must be 5-20 characters: digits, spaces, + - ( )."));
    }

    @Test
    void emailFormatIsChecked() {

        Patient patient = validPatient();
        patient.setEmail("not-an-email");

        assertTrue(PatientValidator.validate(patient)
                .contains("Enter a valid email address (100 characters or fewer)."));
    }

    @Test
    void addressLengthIsLimited() {

        Patient patient = validPatient();
        patient.setAddress("a".repeat(256));

        assertTrue(PatientValidator.validate(patient)
                .contains("Address must be 255 characters or fewer."));
    }

    @Test
    void normalizeTrimsAndBlanksOptionalFields() {

        Patient patient = new Patient();
        patient.setName("  Asha Perera  ");
        patient.setGender("  Female ");
        patient.setBloodGroup(" ");
        patient.setPhone("  ");
        patient.setEmail("");
        patient.setAddress("   ");

        PatientValidator.normalize(patient);

        assertEquals("Asha Perera", patient.getName());
        assertEquals("Female", patient.getGender());
        assertNull(patient.getBloodGroup());
        assertNull(patient.getPhone());
        assertNull(patient.getEmail());
        assertNull(patient.getAddress());
    }

    @Test
    void normalizeLeavesNullUntouched() {

        Patient patient = new Patient();
        patient.setName(null);

        PatientValidator.normalize(patient);

        assertNull(patient.getName());
    }

    @Test
    void invalidPatientReportsEveryProblem() {

        Patient patient = new Patient();
        patient.setAge(0);

        List<String> errors = PatientValidator.validate(patient);

        assertTrue(errors.contains("Full name is required."));
        assertTrue(errors.contains("Age must be between 1 and 120."));
    }

    @Test
    void errorMessagesAreStable() {

        assertFalse(PatientValidator.validate(validPatient()).contains(
                "Full name is required."));
    }
}
