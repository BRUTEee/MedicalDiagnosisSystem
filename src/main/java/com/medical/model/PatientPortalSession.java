package com.medical.model;

import java.io.Serializable;

public final class PatientPortalSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String patientId;
    private final String displayName;

    public PatientPortalSession(String patientId, String displayName) {
        this.patientId = patientId;
        this.displayName = displayName;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDisplayName() {
        return displayName;
    }
}