package com.medical.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.medical.dao.DiagnosisDAO;
import com.medical.dao.PatientDAO;
import com.medical.model.Diagnosis;
import com.medical.model.Patient;
import com.medical.util.Flash;
import com.medical.util.SymptomAnalyzer;

/**
 * Symptom assessments.
 *
 * GET  /diagnosis             new assessment form (optional ?patientId=)
 * GET  /diagnosis?id=         a saved assessment record
 * POST /diagnosis             save, then redirect to the saved record
 */
@WebServlet("/diagnosis")
public class DiagnosisServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final int MAX_SYMPTOMS_LENGTH = 5000;

    private static final String FORM_VIEW =
            "/WEB-INF/views/diagnosis-assistance.jsp";
    private static final String RESULT_VIEW =
            "/WEB-INF/views/diagnosis-results.jsp";

    private static final String RECOMMENDATION =
            "This is a preliminary, rule-based symptom review, not a confirmed "
          + "diagnosis. A qualified healthcare professional must evaluate the "
          + "patient before deciding on diagnosis or treatment.";

    private static final String URGENT_RECOMMENDATION =
            "Warning-sign keywords were found in the reported symptoms. "
          + "Arrange prompt clinical assessment; use emergency services if "
          + "the symptoms are current or severe. ";

    private PatientDAO patientDAO;
    private DiagnosisDAO diagnosisDAO;
    private SymptomAnalyzer symptomAnalyzer;

    @Override
    public void init() {
        patientDAO = new PatientDAO();
        diagnosisDAO = new DiagnosisDAO();
        symptomAnalyzer = new SymptomAnalyzer();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String idParameter = request.getParameter("id");

        if (idParameter == null) {
            showForm(request, response, null);
            return;
        }

        int id = PatientServlet.parseId(idParameter);

        Diagnosis diagnosis = id > 0
                ? diagnosisDAO.getDiagnosisById(id)
                : null;

        if (diagnosis == null) {
            response.sendError(
                HttpServletResponse.SC_NOT_FOUND, "Assessment not found.");
            return;
        }

        request.setAttribute("diagnosis", diagnosis);
        request.setAttribute("patient",
                patientDAO.getPatientById(diagnosis.getPatientId()));
        request.setAttribute("urgent",
                !symptomAnalyzer.findRedFlags(diagnosis.getSymptoms())
                                .isEmpty());

        request.getRequestDispatcher(RESULT_VIEW).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        int patientId =
                PatientServlet.parseId(request.getParameter("patientId"));

        String symptoms = request.getParameter("symptoms");
        symptoms = symptoms == null ? "" : symptoms.trim();

        String error = null;

        if (patientId <= 0 || patientDAO.getPatientById(patientId) == null) {
            error = "Please select a valid patient.";
        } else if (symptoms.isEmpty()) {
            error = "Please record the reported symptoms.";
        } else if (symptoms.length() > MAX_SYMPTOMS_LENGTH) {
            error = "Symptoms must be " + MAX_SYMPTOMS_LENGTH
                  + " characters or fewer.";
        }

        if (error != null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("symptoms", symptoms);
            showForm(request, response, error);
            return;
        }

        boolean urgent = !symptomAnalyzer.findRedFlags(symptoms).isEmpty();

        Diagnosis diagnosis = new Diagnosis();
        diagnosis.setPatientId(patientId);
        diagnosis.setSymptoms(symptoms);
        diagnosis.setPossibleConditions(
                String.join("\n", symptomAnalyzer.analyzeSymptoms(symptoms)));
        diagnosis.setRecommendations(
                (urgent ? URGENT_RECOMMENDATION : "") + RECOMMENDATION);

        int id = diagnosisDAO.addDiagnosis(diagnosis);

        Flash.success(request, "Assessment saved.");

        // Redirect so that refreshing the page does not save a duplicate.
        response.sendRedirect(request.getContextPath()
                + (id > 0 ? "/diagnosis?id=" + id
                          : "/patient-details?id=" + patientId));
    }

    private void showForm(HttpServletRequest request,
                          HttpServletResponse response,
                          String error)
            throws ServletException, IOException {

        List<Patient> patients = patientDAO.getAllPatients();

        request.setAttribute("patients", patients);
        request.setAttribute("error", error);
        request.setAttribute("selectedPatientId",
                PatientServlet.parseId(request.getParameter("patientId")));

        request.getRequestDispatcher(FORM_VIEW).forward(request, response);
    }
}
