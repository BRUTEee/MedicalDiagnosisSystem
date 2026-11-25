package com.medical.model;

import java.sql.Timestamp;

public class Diagnosis {

    private int id;
    private int patientId;
    private String symptoms;
    private String possibleConditions;
    private String recommendations;
    private Timestamp createdAt;
    private String patientName;

    // Default constructor
    public Diagnosis() {
    }

    // Parameterized constructor
    public Diagnosis(int id, int patientId, String symptoms,
                     String possibleConditions, String recommendations,
                     Timestamp createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.symptoms = symptoms;
        this.possibleConditions = possibleConditions;
        this.recommendations = recommendations;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getPossibleConditions() {
        return possibleConditions;
    }

    public void setPossibleConditions(String possibleConditions) {
        this.possibleConditions = possibleConditions;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }

    /** Patient name, populated by queries that join the patients table. */
    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}