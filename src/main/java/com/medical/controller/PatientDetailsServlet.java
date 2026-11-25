package com.medical.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.medical.dao.AppointmentDAO;
import com.medical.dao.DiagnosisDAO;
import com.medical.dao.PatientDAO;
import com.medical.model.Patient;

@WebServlet("/patient-details")
public class PatientDetailsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PatientDAO patientDAO;
    private DiagnosisDAO diagnosisDAO;
    private AppointmentDAO appointmentDAO;

    @Override
    public void init() {
        patientDAO = new PatientDAO();
        diagnosisDAO = new DiagnosisDAO();
        appointmentDAO = new AppointmentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        int patientId = PatientServlet.parseId(request.getParameter("id"));

        if (patientId <= 0) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST, "Invalid patient ID.");
            return;
        }

        Patient patient = patientDAO.getPatientById(patientId);

        if (patient == null) {
            response.sendError(
                HttpServletResponse.SC_NOT_FOUND, "Patient not found.");
            return;
        }

        boolean appointmentsAvailable = appointmentDAO.isAvailable();

        request.setAttribute("patient", patient);
        request.setAttribute("assessments",
                diagnosisDAO.getDiagnosesByPatientId(patientId));
        request.setAttribute("appointmentsAvailable", appointmentsAvailable);
        request.setAttribute("appointments",
                appointmentsAvailable
                    ? appointmentDAO.getAppointmentsByPatientId(patientId)
                    : List.of());

        request.getRequestDispatcher("/WEB-INF/views/patient-details.jsp")
               .forward(request, response);
    }
}
