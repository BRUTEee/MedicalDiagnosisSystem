package com.medical.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.medical.dao.DiagnosisDAO;
import com.medical.dao.PatientDAO;
import com.medical.model.Diagnosis;

/**
 * Reports.
 *
 * GET /reports              summary statistics and all assessment records
 * GET /reports?format=csv   assessment records as a CSV download
 */
@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private PatientDAO patientDAO;
    private DiagnosisDAO diagnosisDAO;

    @Override
    public void init() {
        patientDAO = new PatientDAO();
        diagnosisDAO = new DiagnosisDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        List<Diagnosis> assessments = diagnosisDAO.getAllDiagnoses();

        if ("csv".equals(request.getParameter("format"))) {
            writeCsv(response, assessments);
            return;
        }

        request.setAttribute("assessments", assessments);
        request.setAttribute("patientCount", patientDAO.getPatientCount());
        request.setAttribute("genderCounts", patientDAO.countByGender());
        request.setAttribute("bloodGroupCounts",
                patientDAO.countByBloodGroup());

        request.getRequestDispatcher("/WEB-INF/views/reports.jsp")
               .forward(request, response);
    }

    private void writeCsv(HttpServletResponse response,
                          List<Diagnosis> assessments)
            throws IOException {

        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"assessments.csv\"");

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        PrintWriter out = response.getWriter();

        // Byte-order mark so spreadsheet applications detect UTF-8.
        out.write('\uFEFF');
        out.write("Assessment ID,Patient ID,Patient,Recorded At,"
                + "Reported Symptoms,Rule-Based Review,Recommendations\r\n");

        for (Diagnosis d : assessments) {
            out.write(d.getId() + "," + d.getPatientId() + ","
                    + csv(d.getPatientName()) + ","
                    + csv(d.getCreatedAt() == null
                            ? "" : format.format(d.getCreatedAt())) + ","
                    + csv(d.getSymptoms()) + ","
                    + csv(d.getPossibleConditions()) + ","
                    + csv(d.getRecommendations()) + "\r\n");
        }
    }

    /**
     * Quotes a CSV field. Values that a spreadsheet would interpret as a
     * formula are prefixed with an apostrophe (CSV injection protection).
     */
    static String csv(String value) {

        if (value == null) {
            return "\"\"";
        }

        String text = value;

        if (!text.isEmpty() && "=+-@\t\r".indexOf(text.charAt(0)) >= 0) {
            text = "'" + text;
        }

        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
