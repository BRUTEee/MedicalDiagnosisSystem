-- Medical Diagnosis Assistance System - full schema
--
-- Run as a MySQL administrator (root):
--   mysql -u root -p < Database/schema.sql
--
-- The script is idempotent: it can be run against an empty server or on top
-- of an existing installation created by an earlier version of this file.

CREATE DATABASE IF NOT EXISTS medical_diagnosis
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE medical_diagnosis;

CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'DOCTOR'
);

CREATE TABLE IF NOT EXISTS patients (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(20),
    blood_group VARCHAR(10),
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS diagnoses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    patient_id INT NOT NULL,
    symptoms TEXT NOT NULL,
    possible_conditions TEXT,
    recommendations TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_diagnoses_patient
        FOREIGN KEY (patient_id) REFERENCES patients (id)
        ON DELETE RESTRICT
);

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

-- Application database user.
--
-- Choose a strong password and put the same value in
-- WebContent/WEB-INF/app.properties (or the MDS_DB_PASSWORD environment
-- variable). The placeholder below must be replaced before use.
CREATE USER IF NOT EXISTS 'medical_app'@'localhost'
    IDENTIFIED BY 'CHANGE_ME_STRONG_PASSWORD';

GRANT SELECT
    ON medical_diagnosis.users
    TO 'medical_app'@'localhost';

GRANT UPDATE (name, password)
    ON medical_diagnosis.users
    TO 'medical_app'@'localhost';

GRANT SELECT, INSERT, UPDATE, DELETE
    ON medical_diagnosis.patients
    TO 'medical_app'@'localhost';

GRANT SELECT, INSERT
    ON medical_diagnosis.diagnoses
    TO 'medical_app'@'localhost';

GRANT SELECT, INSERT, UPDATE
    ON medical_diagnosis.appointments
    TO 'medical_app'@'localhost';

FLUSH PRIVILEGES;
