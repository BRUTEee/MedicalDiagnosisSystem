package com.medical.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/medical_diagnosis";

    private static final String DEFAULT_USER = "medical_app";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Reported as an SQLException on the first connection attempt.
        }
    }

    private DBConnection() {
    }

    /**
     * Opens a new connection. Callers must close it (try-with-resources).
     *
     * @throws SQLException when the database is unreachable or not configured
     */
    public static Connection getConnection() throws SQLException {

        String password = AppConfig.get("db.password", null);

        if (password == null) {
            throw new SQLException(
                    "Database password is not configured. Set db.password in "
                    + "WEB-INF/app.properties or the MDS_DB_PASSWORD environment variable.");
        }

        return DriverManager.getConnection(
                AppConfig.get("db.url", DEFAULT_URL),
                AppConfig.get("db.username", DEFAULT_USER),
                password);
    }
}
