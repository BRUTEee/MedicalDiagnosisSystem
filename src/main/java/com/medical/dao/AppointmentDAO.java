package com.medical.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.medical.model.Appointment;
import com.medical.util.DBConnection;

public class AppointmentDAO {

    private static final String SELECT =
            "SELECT a.*, p.name AS patient_name "
          + "FROM appointments a JOIN patients p ON p.id = a.patient_id ";

    /**
     * Returns false when the appointments table has not been created yet
     * (databases set up before the appointments module was added).
     */
    public boolean isAvailable() {

        String sql = "SELECT 1 FROM appointments LIMIT 1";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            return statement.execute();

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean addAppointment(Appointment appointment) {

        String sql = "INSERT INTO appointments "
                   + "(patient_id, scheduled_at, reason, status) "
                   + "VALUES (?, ?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, appointment.getPatientId());
            statement.setTimestamp(2, appointment.getScheduledAt());
            statement.setString(3, appointment.getReason());
            statement.setString(4, Appointment.SCHEDULED);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Unable to save the appointment.", e);
        }
    }

    /** Appointments, optionally limited to one status; soonest first. */
    public List<Appointment> getAppointments(String status) {

        if (status == null) {
            return query(SELECT + "ORDER BY a.scheduled_at DESC, a.id DESC");
        }

        return query(
                SELECT + "WHERE a.status = ? "
                       + "ORDER BY a.scheduled_at ASC, a.id ASC",
                status);
    }

    /** Scheduled appointments that have not started yet. */
    public List<Appointment> getUpcomingAppointments(int limit) {
        return query(
                SELECT + "WHERE a.status = 'SCHEDULED' "
                       + "AND a.scheduled_at >= NOW() "
                       + "ORDER BY a.scheduled_at ASC LIMIT " + Math.max(1, limit));
    }

    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        return query(
                SELECT + "WHERE a.patient_id = ? "
                       + "ORDER BY a.scheduled_at DESC, a.id DESC",
                patientId);
    }

    public boolean hasAppointmentsByPatientId(int patientId) {

        String sql = "SELECT 1 FROM appointments WHERE patient_id = ? LIMIT 1";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, patientId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {
            throw new DataAccessException(
                    "Unable to check the patient's appointments.", e);
        }
    }

    public int getUpcomingCount() {

        String sql = "SELECT COUNT(*) FROM appointments "
                   + "WHERE status = 'SCHEDULED' AND scheduled_at >= NOW()";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {

            return resultSet.next() ? resultSet.getInt(1) : 0;

        } catch (SQLException e) {
            throw new DataAccessException("Unable to count appointments.", e);
        }
    }

    /** Marks a scheduled appointment as completed or cancelled. */
    public boolean updateStatus(int id, String status) {

        String sql = "UPDATE appointments SET status = ? "
                   + "WHERE id = ? AND status = 'SCHEDULED'";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, status);
            statement.setInt(2, id);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new DataAccessException("Unable to update the appointment.", e);
        }
    }

    private List<Appointment> query(String sql, Object... parameters) {

        List<Appointment> appointments = new ArrayList<>();

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
            }

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    Appointment appointment = new Appointment();

                    appointment.setId(resultSet.getInt("id"));
                    appointment.setPatientId(resultSet.getInt("patient_id"));
                    appointment.setPatientName(
                        resultSet.getString("patient_name"));
                    appointment.setScheduledAt(
                        resultSet.getTimestamp("scheduled_at"));
                    appointment.setReason(resultSet.getString("reason"));
                    appointment.setStatus(resultSet.getString("status"));
                    appointment.setCreatedAt(
                        resultSet.getTimestamp("created_at"));

                    appointments.add(appointment);
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to load appointments.", e);
        }

        return appointments;
    }
}
