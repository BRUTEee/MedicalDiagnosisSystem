package com.medical.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.medical.model.User;
import com.medical.util.DBConnection;
import com.medical.util.PasswordUtil;

public class UserDAO {

    /**
     * Returns the user when the credentials are correct, otherwise null.
     * Accounts that still hold a legacy plain-text password are re-hashed
     * transparently on their first successful sign-in.
     */
    public User authenticate(String email, String password) {

        if (email == null || password == null
                || email.isBlank() || password.isEmpty()) {
            return null;
        }

        String sql = "SELECT id, name, email, password, role "
                   + "FROM users WHERE email = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, email.trim());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (!resultSet.next()) {
                    return null;
                }

                String stored = resultSet.getString("password");

                if (!PasswordUtil.verify(password, stored)) {
                    return null;
                }

                User user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("email"),
                        resultSet.getString("role"));

                if (!PasswordUtil.isHashed(stored)) {
                    storePassword(connection, user.getId(), password);
                }

                return user;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Unable to verify the sign-in.", e);
        }
    }

    /** Changes the password after verifying the current one. */
    public boolean changePassword(int userId,
                                  String currentPassword,
                                  String newPassword) {

        String sql = "SELECT password FROM users WHERE id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (!resultSet.next() || !PasswordUtil.verify(
                        currentPassword, resultSet.getString("password"))) {
                    return false;
                }
            }

            storePassword(connection, userId, newPassword);
            return true;

        } catch (SQLException e) {
            throw new DataAccessException("Unable to change the password.", e);
        }
    }

    public boolean updateName(int userId, String name) {

        String sql = "UPDATE users SET name = ? WHERE id = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, name);
            statement.setInt(2, userId);
            statement.executeUpdate();
            return true;

        } catch (SQLException e) {
            throw new DataAccessException("Unable to update the profile.", e);
        }
    }

    private void storePassword(Connection connection,
                               int userId, String password)
            throws SQLException {

        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, PasswordUtil.hash(password));
            statement.setInt(2, userId);
            statement.executeUpdate();
        }
    }
}
