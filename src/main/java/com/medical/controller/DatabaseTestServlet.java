package com.medical.controller;

import java.io.IOException;
import java.sql.Connection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.medical.util.DBConnection;

@WebServlet("/database-test")
public class DatabaseTestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        Connection connection = DBConnection.getConnection();

        response.setContentType("text/html");

        if (connection != null) {

            response.getWriter().println(
                "<h1>Database Connected Successfully!</h1>"
            );

        } else {

            response.getWriter().println(
                "<h1>Database Connection Failed!</h1>"
            );
        }
    }
}