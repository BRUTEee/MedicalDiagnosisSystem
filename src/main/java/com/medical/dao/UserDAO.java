package com.medical.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.medical.model.User;
import com.medical.util.DBConnection;

public class UserDAO {

    public User login(String email, String password) {

        User user = null;

        String sql =
            "SELECT * FROM users WHERE email = ? AND password = ?";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                connection.prepareStatement(sql)
        ) {

            statement.setString(1, email);
            statement.setString(2, password);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {

                user = new User();

                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                user.setRole(resultSet.getString("role"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;

	}
}