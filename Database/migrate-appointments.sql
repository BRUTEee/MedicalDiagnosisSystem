-- Migration: add the appointments module.
--
-- Run as a MySQL administrator (root) on a database created by an earlier
-- version of schema.sql:
--   mysql -u root -p medical_diagnosis < Database/migrate-appointments.sql
--
-- Safe to run more than once.

USE medical_diagnosis;

CREATE TABLE IF NOT EXISTS appointments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    scheduled_at DATETIME NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_appointments_patient
        FOREIGN KEY (patient_id) REFERENCES patients (id)
        ON DELETE RESTRICT,
    INDEX idx_appointments_patient (patient_id),
    INDEX idx_appointments_scheduled (status, scheduled_at)
);
