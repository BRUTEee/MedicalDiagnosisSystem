package com.medical.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.medical.model.Patient;
import com.medical.util.DBConnection;

public class PatientDAO {

    /** Inserts the patient and returns the generated ID. */
    public int addPatient(Patient patient) {

        String sql = "INSERT INTO patients "
                   + "(name, age, gender, blood_group, phone, email, address) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            bind(statement, patient);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to add the patient.", e);
        }
    }

    public List<Patient> getAllPatients() {
        return searchPatients(null);
    }

    /** Lists patients, optionally filtered by name, phone, email or ID. */
    public List<Patient> searchPatients(String query) {

        boolean filtered = query != null && !query.isBlank();

        String sql = "SELECT * FROM patients "
                   + (filtered
                       ? "WHERE name LIKE ? OR phone LIKE ? OR email LIKE ? "
                         + "OR CAST(id AS CHAR) = ? "
                       : "")
                   + "ORDER BY id DESC";

        List<Patient> patients = new ArrayList<>();

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            if (filtered) {
                String term = query.trim();

                String pattern = "%" + term
                        .replace("\\", "\\\\")
                        .replace("%", "\\%")
                        .replace("_", "\\_") + "%";

                statement.setString(1, pattern);
                statement.setString(2, pattern);
                statement.setString(3, pattern);
                statement.setString(4, term);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    patients.add(map(resultSet));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to load patients.", e);
        }

        return patients;
    }

    public int getPatientCount() {

        String sql = "SELECT COUNT(*) FROM patients";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {

            return resultSet.next() ? resultSet.getInt(1) : 0;

        } catch (SQLException e) {
            throw new DataAccessException("Unable to count patients.", e);
        }
    }

    /** Number of patients per gender, for the reports page. */
    public Map<String, Integer> countByGender() {
        return countBy("gender");
    }

    /** Number of patients per blood group, for the reports page. */
    public Map<String, Integer> countByBloodGroup() {
        return countBy("blood_group");
    }

    private Map<String, Integer> countBy(String column) {

        // The column name is one of two constants above, never user input.
        String sql = "SELECT COALESCE(NULLIF(" + column + ", ''), 'Not recorded') "
                   + "AS label, COUNT(*) AS total FROM patients "
                   + "GROUP BY label ORDER BY total DESC, label";

        Map<String, Integer> counts = new LinkedHashMap<>();

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {
                counts.put(resultSet.getString("label"),
                           resultSet.getInt("total"));
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to load patient statistics.", e);
        }

        return counts;
    }

    public Patient getPatientById(int id) {

        String sql = "SELECT * FROM patients WHERE id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? map(resultSet) : null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to load the patient.", e);
        }
    }

    /** Returns false when no patient has the given ID. */
    public boolean updatePatient(Patient patient) {

        String sql = "UPDATE patients SET "
                   + "name = ?, age = ?, gender = ?, blood_group = ?, "
                   + "phone = ?, email = ?, address = ? "
                   + "WHERE id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            bind(statement, patient);
            statement.setInt(8, patient.getId());

            // MySQL reports zero rows when the values are unchanged, so
            // confirm existence separately in that case.
            if (statement.executeUpdate() > 0) {
                return true;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to update the patient.", e);
        }

        return getPatientById(patient.getId()) != null;
    }

    /**
     * Deletes a patient. Callers must first check that the patient has no
     * assessment records; the database also enforces this (ON DELETE RESTRICT).
     */
    public boolean deletePatient(int id) {

        String sql = "DELETE FROM patients WHERE id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);
            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete the patient.", e);
        }
    }

    private void bind(PreparedStatement statement, Patient patient)
            throws SQLException {

        statement.setString(1, patient.getName());
        statement.setInt(2, patient.getAge());
        statement.setString(3, patient.getGender());
        statement.setString(4, patient.getBloodGroup());
        statement.setString(5, patient.getPhone());
        statement.setString(6, patient.getEmail());
        statement.setString(7, patient.getAddress());
    }

    private Patient map(ResultSet resultSet) throws SQLException {

        Patient patient = new Patient();

        patient.setId(resultSet.getInt("id"));
        patient.setName(resultSet.getString("name"));
        patient.setAge(resultSet.getInt("age"));
        patient.setGender(resultSet.getString("gender"));
        patient.setBloodGroup(resultSet.getString("blood_group"));
        patient.setPhone(resultSet.getString("phone"));
        patient.setEmail(resultSet.getString("email"));
        patient.setAddress(resultSet.getString("address"));
        patient.setCreatedAt(resultSet.getTimestamp("created_at"));

        return patient;
    }
}
