package com.medical.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.medical.model.Diagnosis;
import com.medical.util.DBConnection;

public class DiagnosisDAO {

    private static final String SELECT =
            "SELECT d.*, p.name AS patient_name "
          + "FROM diagnoses d JOIN patients p ON p.id = d.patient_id ";

    /** Saves an assessment record and returns the generated ID. */
    public int addDiagnosis(Diagnosis diagnosis) {

        String sql = "INSERT INTO diagnoses "
                   + "(patient_id, symptoms, possible_conditions, recommendations) "
                   + "VALUES (?, ?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            statement.setInt(1, diagnosis.getPatientId());
            statement.setString(2, diagnosis.getSymptoms());
            statement.setString(3, diagnosis.getPossibleConditions());
            statement.setString(4, diagnosis.getRecommendations());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to save the assessment.", e);
        }
    }

    /** Assessment records for one patient, newest first. */
    public List<Diagnosis> getDiagnosesByPatientId(int patientId) {
        return query(
                SELECT + "WHERE d.patient_id = ? "
                       + "ORDER BY d.created_at DESC, d.id DESC",
                patientId);
    }

    /** The most recent assessment records across all patients. */
    public List<Diagnosis> getRecentDiagnoses(int limit) {
        return query(
                SELECT + "ORDER BY d.created_at DESC, d.id DESC LIMIT ?",
                limit);
    }

    /** Every assessment record, newest first. */
    public List<Diagnosis> getAllDiagnoses() {
        return query(SELECT + "ORDER BY d.created_at DESC, d.id DESC");
    }

    public Diagnosis getDiagnosisById(int id) {

        List<Diagnosis> result = query(SELECT + "WHERE d.id = ?", id);

        return result.isEmpty() ? null : result.get(0);
    }

    public int getDiagnosisCount() {
        return count("SELECT COUNT(*) FROM diagnoses");
    }

    public int getDiagnosisCountToday() {
        return count("SELECT COUNT(*) FROM diagnoses "
                   + "WHERE created_at >= CURRENT_DATE");
    }

    public int getDiagnosisCountForPatient(int patientId) {
        return count("SELECT COUNT(*) FROM diagnoses WHERE patient_id = ?",
                     patientId);
    }

    private int count(String sql, int... parameters) {

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            for (int i = 0; i < parameters.length; i++) {
                statement.setInt(i + 1, parameters[i]);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? resultSet.getInt(1) : 0;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to count assessments.", e);
        }
    }

    private List<Diagnosis> query(String sql, int... parameters) {

        List<Diagnosis> diagnoses = new ArrayList<>();

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            for (int i = 0; i < parameters.length; i++) {
                statement.setInt(i + 1, parameters[i]);
            }

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Diagnosis diagnosis = new Diagnosis();

                    diagnosis.setId(resultSet.getInt("id"));
                    diagnosis.setPatientId(resultSet.getInt("patient_id"));
                    diagnosis.setPatientName(
                        resultSet.getString("patient_name"));
                    diagnosis.setSymptoms(resultSet.getString("symptoms"));
                    diagnosis.setPossibleConditions(
                        resultSet.getString("possible_conditions"));
                    diagnosis.setRecommendations(
                        resultSet.getString("recommendations"));
                    diagnosis.setCreatedAt(
                        resultSet.getTimestamp("created_at"));

                    diagnoses.add(diagnosis);
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to load assessments.", e);
        }

        return diagnoses;
    }
}
