package com.medical.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.medical.dao.AppointmentDAO;
import com.medical.dao.PatientDAO;
import com.medical.model.Appointment;
import com.medical.util.Flash;

/**
 * Appointments.
 *
 * GET  /appointments            list (optional ?status=SCHEDULED|COMPLETED|CANCELLED)
 * POST /appointments action=create | complete | cancel
 */
@WebServlet("/appointments")
public class AppointmentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final int MAX_REASON_LENGTH = 255;

    private static final String VIEW = "/WEB-INF/views/appointments.jsp";

    private AppointmentDAO appointmentDAO;
    private PatientDAO patientDAO;

    @Override
    public void init() {
        appointmentDAO = new AppointmentDAO();
        patientDAO = new PatientDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        showPage(request, response, null);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        if (!appointmentDAO.isAvailable()) {
            response.sendRedirect(request.getContextPath() + "/appointments");
            return;
        }

        String action = request.getParameter("action");

        if ("complete".equals(action) || "cancel".equals(action)) {

            int id = PatientServlet.parseId(request.getParameter("id"));

            String status = "complete".equals(action)
                    ? Appointment.COMPLETED
                    : Appointment.CANCELLED;

            if (id > 0 && appointmentDAO.updateStatus(id, status)) {
                Flash.success(request, "Appointment marked as "
                        + status.toLowerCase(Locale.ROOT) + ".");
            } else {
                Flash.error(request,
                    "That appointment could not be updated. "
                    + "It may already be completed or cancelled.");
            }

            response.sendRedirect(request.getContextPath() + "/appointments");
            return;
        }

        createAppointment(request, response);
    }

    private void createAppointment(HttpServletRequest request,
                                   HttpServletResponse response)
            throws ServletException, IOException {

        int patientId =
                PatientServlet.parseId(request.getParameter("patientId"));

        String reason = request.getParameter("reason");
        reason = reason == null ? "" : reason.trim();

        LocalDateTime scheduledAt = null;

        try {
            String value = request.getParameter("scheduledAt");

            if (value != null && !value.isBlank()) {
                scheduledAt = LocalDateTime.parse(value.trim());
            }
        } catch (DateTimeParseException e) {
            // Reported below as an invalid date.
        }

        String error = null;

        if (patientId <= 0 || patientDAO.getPatientById(patientId) == null) {
            error = "Please select a valid patient.";
        } else if (scheduledAt == null) {
            error = "Please enter a valid date and time.";
        } else if (!scheduledAt.isAfter(LocalDateTime.now())) {
            error = "The appointment time must be in the future.";
        } else if (scheduledAt.isAfter(LocalDateTime.now().plusYears(2))) {
            error = "The appointment time must be within the next two years.";
        } else if (reason.length() > MAX_REASON_LENGTH) {
            error = "Reason must be " + MAX_REASON_LENGTH
                  + " characters or fewer.";
        }

        if (error != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("formReason", reason);
            request.setAttribute("formScheduledAt",
                    request.getParameter("scheduledAt"));
            if ("true".equals(request.getParameter("returnToPatient"))) {
                request.setAttribute("returnToPatient", Boolean.TRUE);
            }
            showPage(request, response, error);
            return;
        }

        Appointment appointment = new Appointment();
        appointment.setPatientId(patientId);
        appointment.setScheduledAt(Timestamp.valueOf(scheduledAt));
        appointment.setReason(reason.isEmpty() ? null : reason);

        if (!appointmentDAO.addAppointment(appointment)) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            request.setAttribute("formReason", reason);
            request.setAttribute("formScheduledAt",
                    request.getParameter("scheduledAt"));
            request.setAttribute("returnToPatient",
                    "true".equals(request.getParameter("returnToPatient")));
            showPage(request, response,
                    "The appointment could not be saved. Please try again.");
            return;
        }

        Flash.success(request, "Appointment scheduled.");
        String destination = "true".equals(
                request.getParameter("returnToPatient"))
                ? "/patient-details?id=" + patientId
                : "/appointments";
        response.sendRedirect(request.getContextPath() + destination);
    }

    private void showPage(HttpServletRequest request,
                          HttpServletResponse response,
                          String error)
            throws ServletException, IOException {

        boolean available = appointmentDAO.isAvailable();

        String status = request.getParameter("status");

        if (status != null) {
            status = status.toUpperCase(Locale.ROOT);

            if (!Appointment.STATUSES.contains(status)) {
                status = null;
            }
        }

        request.setAttribute("appointmentsAvailable", available);
        request.setAttribute("status", status);
        request.setAttribute("error", error);
        int requestedPatientId = PatientServlet.parseId(
                request.getParameter("patientId"));
        int selectedPatientId = requestedPatientId > 0
                && patientDAO.getPatientById(requestedPatientId) != null
                    ? requestedPatientId : 0;
        request.setAttribute("selectedPatientId", selectedPatientId);
        request.setAttribute("returnToPatient",
                selectedPatientId > 0 && (Boolean.TRUE.equals(
                        request.getAttribute("returnToPatient"))
                        || "true".equals(request.getParameter("returnToPatient"))));

        List<Appointment> appointmentsList;
        if (available) {
            if (selectedPatientId > 0) {
                appointmentsList = appointmentDAO.getAppointmentsByPatientId(selectedPatientId);
                // Filter by status if provided
                if (status != null) {
                    final String filterStatus = status;
                    appointmentsList = appointmentsList.stream()
                            .filter(a -> filterStatus.equals(a.getStatus()))
                            .toList();
                }
            } else {
                appointmentsList = appointmentDAO.getAppointments(status);
            }
        } else {
            appointmentsList = List.of();
        }
        request.setAttribute("appointments", appointmentsList);
        request.setAttribute("patients",
                available ? patientDAO.getAllPatients() : List.of());

        request.getRequestDispatcher(VIEW).forward(request, response);
    }
}
