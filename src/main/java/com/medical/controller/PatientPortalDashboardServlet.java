package com.medical.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.medical.dao.AppointmentDAO;
import com.medical.dao.DiagnosisDAO;
import com.medical.dao.PatientDAO;
import com.medical.model.Appointment;
import com.medical.model.Diagnosis;
import com.medical.model.Patient;
import com.medical.model.PatientPortalSession;

@WebServlet("/patient-dashboard")
public class PatientPortalDashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PatientDAO patientDAO;
    private AppointmentDAO appointmentDAO;
    private DiagnosisDAO diagnosisDAO;

    @Override
    public void init() {
        patientDAO = new PatientDAO();
        appointmentDAO = new AppointmentDAO();
        diagnosisDAO = new DiagnosisDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        PatientPortalSession portalSession = (PatientPortalSession)
                session.getAttribute("patientPortal");

        if (portalSession == null) {
            response.sendRedirect(request.getContextPath() + "/patient-login");
            return;
        }

        int patientId = Integer.parseInt(portalSession.getPatientId());
        Patient patient = patientDAO.getPatientById(patientId);

        if (patient == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Patient not found.");
            return;
        }

        List<Appointment> upcomingAppointments = List.of();
        List<Appointment> pastAppointments = List.of();
        List<Diagnosis> assessments = List.of();

        if (appointmentDAO.isAvailable()) {
            List<Appointment> allAppointments = appointmentDAO.getAppointmentsByPatientId(patientId);
            for (Appointment appt : allAppointments) {
                if (Appointment.SCHEDULED.equals(appt.getStatus()) && appt.getScheduledAt().after(new java.util.Date())) {
                    upcomingAppointments.add(appt);
                } else {
                    pastAppointments.add(appt);
                }
            }
        }

        assessments = diagnosisDAO.getDiagnosesByPatientId(patientId);

        request.setAttribute("patientPortal", portalSession);
        request.setAttribute("patient", patient);
        request.setAttribute("upcomingAppointments", upcomingAppointments);
        request.setAttribute("pastAppointments", pastAppointments);
        request.setAttribute("assessments", assessments);

        request.getRequestDispatcher("/WEB-INF/views/patient-dashboard.jsp")
                .forward(request, response);
    }
}