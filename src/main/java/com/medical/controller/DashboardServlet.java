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

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

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

        request.setAttribute("patientCount", patientDAO.getPatientCount());
        request.setAttribute("assessmentCount",
                diagnosisDAO.getDiagnosisCount());
        request.setAttribute("assessmentsToday",
                diagnosisDAO.getDiagnosisCountToday());
        request.setAttribute("recentAssessments",
                diagnosisDAO.getRecentDiagnoses(5));

        boolean appointmentsAvailable = appointmentDAO.isAvailable();

        request.setAttribute("appointmentsAvailable", appointmentsAvailable);

        if (appointmentsAvailable) {
            request.setAttribute("upcomingCount",
                    appointmentDAO.getUpcomingCount());
            request.setAttribute("upcomingAppointments",
                    appointmentDAO.getUpcomingAppointments(5));
        } else {
            request.setAttribute("upcomingCount", 0);
            request.setAttribute("upcomingAppointments", List.of());
        }

        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp")
               .forward(request, response);
    }
}
