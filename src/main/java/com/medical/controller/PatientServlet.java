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
import com.medical.util.Flash;
import com.medical.util.PatientValidator;

/**
 * Patient list, registration, editing and deletion.
 *
 * GET  /patients                 list (optional ?q= search)
 * GET  /patients?action=new      registration form
 * GET  /patients?action=edit&id= edit form
 * POST /patients action=create | update | delete
 */
@WebServlet("/patients")
public class PatientServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String LIST_VIEW = "/WEB-INF/views/patients.jsp";
    private static final String FORM_VIEW = "/WEB-INF/views/patient-form.jsp";

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

        String action = request.getParameter("action");

        if ("new".equals(action)) {
            request.setAttribute("patient", new Patient());
            request.getRequestDispatcher(FORM_VIEW).forward(request, response);
            return;
        }

        if ("edit".equals(action)) {

            int id = parseId(request.getParameter("id"));

            if (id <= 0) {
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST, "Invalid patient ID.");
                return;
            }

            Patient patient = patientDAO.getPatientById(id);

            if (patient == null) {
                response.sendError(
                    HttpServletResponse.SC_NOT_FOUND, "Patient not found.");
                return;
            }

            request.setAttribute("patient", patient);
            request.getRequestDispatcher(FORM_VIEW).forward(request, response);
            return;
        }

        String query = request.getParameter("q");

        if (query != null && query.length() > 100) {
            query = query.substring(0, 100);
        }

        List<Patient> patients = patientDAO.searchPatients(query);

        request.setAttribute("patients", patients);
        request.setAttribute("query", query == null ? "" : query.trim());

        request.getRequestDispatcher(LIST_VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            deletePatient(request, response);
        } else if ("update".equals(action)) {
            savePatient(request, response, true);
        } else {
            savePatient(request, response, false);
        }
    }

    private void savePatient(HttpServletRequest request,
                             HttpServletResponse response,
                             boolean update)
            throws ServletException, IOException {

        Patient patient = new Patient();

        if (update) {
            int id = parseId(request.getParameter("id"));

            if (id <= 0) {
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST, "Invalid patient ID.");
                return;
            }

            patient.setId(id);
        }

        patient.setName(request.getParameter("name"));
        patient.setAge(parseId(request.getParameter("age")));
        patient.setGender(request.getParameter("gender"));
        patient.setBloodGroup(request.getParameter("bloodGroup"));
        patient.setPhone(request.getParameter("phone"));
        patient.setEmail(request.getParameter("email"));
        patient.setAddress(request.getParameter("address"));

        PatientValidator.normalize(patient);

        List<String> errors = PatientValidator.validate(patient);

        if (!errors.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("errors", errors);
            request.setAttribute("patient", patient);
            request.getRequestDispatcher(FORM_VIEW).forward(request, response);
            return;
        }

        if (update) {

            if (!patientDAO.updatePatient(patient)) {
                response.sendError(
                    HttpServletResponse.SC_NOT_FOUND, "Patient not found.");
                return;
            }

            Flash.success(request, "Patient details updated.");
            response.sendRedirect(request.getContextPath()
                    + "/patient-details?id=" + patient.getId());
            return;
        }

        int id = patientDAO.addPatient(patient);

        Flash.success(request, "Patient registered.");

        response.sendRedirect(request.getContextPath()
                + (id > 0 ? "/patient-details?id=" + id : "/patients"));
    }

    private void deletePatient(HttpServletRequest request,
                               HttpServletResponse response)
            throws IOException {

        int id = parseId(request.getParameter("id"));

        if (id <= 0) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST, "Invalid patient ID.");
            return;
        }

        // Assessment records are part of the clinical history and are never
        // removed implicitly, so a patient who has any cannot be deleted.
        if (diagnosisDAO.getDiagnosisCountForPatient(id) > 0) {
            Flash.error(request,
                "This patient has assessment records and cannot be deleted.");
            response.sendRedirect(request.getContextPath()
                    + "/patient-details?id=" + id);
            return;
        }

        if (appointmentDAO.isAvailable()
                && appointmentDAO.hasAppointmentsByPatientId(id)) {
            Flash.error(request,
                "This patient has appointment records and cannot be deleted.");
            response.sendRedirect(request.getContextPath()
                    + "/patient-details?id=" + id);
            return;
        }

        if (patientDAO.deletePatient(id)) {
            Flash.success(request, "Patient deleted.");
        } else {
            Flash.error(request, "Patient not found.");
        }

        response.sendRedirect(request.getContextPath() + "/patients");
    }

    /** Parses a positive integer; returns 0 when the value is not valid. */
    static int parseId(String value) {

        if (value == null) {
            return 0;
        }

        try {
            return Math.max(0, Integer.parseInt(value.trim()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
