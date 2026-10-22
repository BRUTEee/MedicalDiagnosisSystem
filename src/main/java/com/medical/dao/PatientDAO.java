package com.medical.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.medical.model.Patient;
import com.medical.util.DBConnection;

public class PatientDAO {

    public boolean addPatient(Patient patient) {

        String sql = "INSERT INTO patients "
                   + "(name, age, gender, blood_group, phone, email, address) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, patient.getName());
            statement.setInt(2, patient.getAge());
            statement.setString(3, patient.getGender());
            statement.setString(4, patient.getBloodGroup());
            statement.setString(5, patient.getPhone());
            statement.setString(6, patient.getEmail());
            statement.setString(7, patient.getAddress());

            int rows = statement.executeUpdate();

            return rows > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public List<Patient> getAllPatients() {

        List<Patient> patients = new ArrayList<>();

        String sql = "SELECT * FROM patients ORDER BY id DESC";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Patient patient = new Patient();

                patient.setId(resultSet.getInt("id"));
                patient.setName(resultSet.getString("name"));
                patient.setAge(resultSet.getInt("age"));
                patient.setGender(resultSet.getString("gender"));
                patient.setBloodGroup(resultSet.getString("blood_group"));
                patient.setPhone(resultSet.getString("phone"));
                patient.setEmail(resultSet.getString("email"));
                patient.setAddress(resultSet.getString("address"));

                patients.add(patient);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return patients;
    }
    public int getPatientCount() {

        int count = 0;

        String sql = "SELECT COUNT(*) FROM patients";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {

            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }
    

    public Patient getPatientById(int id) {

        Patient patient = null;

        String sql = "SELECT * FROM patients WHERE id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {

            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    patient = new Patient();

                    patient.setId(resultSet.getInt("id"));
                    patient.setName(resultSet.getString("name"));
                    patient.setAge(resultSet.getInt("age"));
                    patient.setGender(resultSet.getString("gender"));
                    patient.setBloodGroup(resultSet.getString("blood_group"));
                    patient.setPhone(resultSet.getString("phone"));
                    patient.setEmail(resultSet.getString("email"));
                    patient.setAddress(resultSet.getString("address"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return patient;
    }
    

    public boolean updatePatient(Patient patient) {

        String sql = "UPDATE patients SET "
                   + "name = ?, "
                   + "age = ?, "
                   + "gender = ?, "
                   + "blood_group = ?, "
                   + "phone = ?, "
                   + "email = ?, "
                   + "address = ? "
                   + "WHERE id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {

            statement.setString(1, patient.getName());
            statement.setInt(2, patient.getAge());
            statement.setString(3, patient.getGender());
            statement.setString(4, patient.getBloodGroup());
            statement.setString(5, patient.getPhone());
            statement.setString(6, patient.getEmail());
            statement.setString(7, patient.getAddress());
            statement.setInt(8, patient.getId());

            int rows = statement.executeUpdate();

            // The update may affect zero rows if the submitted
            // values are unchanged, so check whether the patient exists.
            if (rows > 0) {
                return true;
            }

            return getPatientById(patient.getId()) != null;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}