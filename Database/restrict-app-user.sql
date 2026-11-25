USE medical_diagnosis;

REVOKE SELECT, INSERT, UPDATE, DELETE
    ON medical_diagnosis.*
    FROM 'medical_app'@'localhost';

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

SHOW GRANTS FOR 'medical_app'@'localhost';
