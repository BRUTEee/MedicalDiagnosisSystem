
package com.medical.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.medical.dao.PatientDAO;
import com.medical.model.Patient;

@WebServlet("/patient-details")
public class PatientDetailsServlet extends HttpServlet {

    private PatientDAO patientDAO;

    @Override
    public void init() {
        patientDAO = new PatientDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Ensure the user is logged in.
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Validate the patient ID.
        String idParameter = request.getParameter("id");
        int patientId;

        try {
            patientId = Integer.parseInt(idParameter);

            if (patientId <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid patient ID."
            );
            return;
        }

        // Retrieve the patient.
        Patient patient = patientDAO.getPatientById(patientId);

        if (patient == null) {
            response.sendError(
                HttpServletResponse.SC_NOT_FOUND,
                "Patient not found."
            );
            return;
        }

        request.setAttribute("patient", patient);

        request.getRequestDispatcher("/patient-details.jsp")
               .forward(request, response);
    }
}
