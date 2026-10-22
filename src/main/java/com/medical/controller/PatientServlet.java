
package com.medical.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.medical.dao.PatientDAO;
import com.medical.model.Patient;
import com.medical.model.User;

@WebServlet("/patients")
public class PatientServlet extends HttpServlet {

    private PatientDAO patientDAO;

    @Override
    public void init() {
        patientDAO = new PatientDAO();
    }

    private boolean isLoggedIn(HttpServletRequest request,
                               HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);

        if (session == null || !(session.getAttribute("user") instanceof User)) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return false;
        }

        return true;
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        if (!isLoggedIn(request, response)) {
            return;
        }

        String action = request.getParameter("action");

        if ("edit".equals(action)) {

            String idParameter = request.getParameter("id");

            try {
                int id = Integer.parseInt(idParameter);

                if (id <= 0) {
                    throw new NumberFormatException();
                }

                Patient patient = patientDAO.getPatientById(id);

                if (patient == null) {
                    response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Patient not found."
                    );
                    return;
                }

                request.setAttribute("patient", patient);

                request.getRequestDispatcher("/edit-patient.jsp")
                       .forward(request, response);

            } catch (NumberFormatException e) {
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid patient ID."
                );
            }

            return;
        }

        List<Patient> patients = patientDAO.getAllPatients();

        request.setAttribute("patients", patients);

        request.getRequestDispatcher("/patients.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        if (!isLoggedIn(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        if ("update".equals(action)) {
            updatePatient(request, response);
            return;
        }

        // Existing add-patient functionality.
        try {
            String name = request.getParameter("name");
            int age = Integer.parseInt(request.getParameter("age"));
            String gender = request.getParameter("gender");
            String bloodGroup = request.getParameter("bloodGroup");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String address = request.getParameter("address");

            if (name == null || name.isBlank()
                    || age < 1 || age > 120) {
                throw new IllegalArgumentException("Invalid patient details.");
            }

            Patient patient = new Patient();
            patient.setName(name.trim());
            patient.setAge(age);
            patient.setGender(gender);
            patient.setBloodGroup(bloodGroup);
            patient.setPhone(phone);
            patient.setEmail(email);
            patient.setAddress(address);

            boolean success = patientDAO.addPatient(patient);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/patients");
            } else {
                request.setAttribute("error", "Unable to add patient.");
                request.getRequestDispatcher("/add-patient.jsp")
                       .forward(request, response);
            }

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Enter a valid name and age.");
            request.getRequestDispatcher("/add-patient.jsp")
                   .forward(request, response);
        }
    }

    private void updatePatient(HttpServletRequest request,
                               HttpServletResponse response)
            throws ServletException, IOException {

        try {
            int id = Integer.parseInt(request.getParameter("id"));
            int age = Integer.parseInt(request.getParameter("age"));

            String name = request.getParameter("name");
            String gender = request.getParameter("gender");
            String bloodGroup = request.getParameter("bloodGroup");
            String phone = request.getParameter("phone");
            String email = request.getParameter("email");
            String address = request.getParameter("address");

            if (id <= 0 || name == null || name.isBlank()
                    || age < 1 || age > 120) {
                throw new IllegalArgumentException("Invalid patient details.");
            }

            Patient patient = new Patient();
            patient.setId(id);
            patient.setName(name.trim());
            patient.setAge(age);
            patient.setGender(gender);
            patient.setBloodGroup(bloodGroup);
            patient.setPhone(phone);
            patient.setEmail(email);
            patient.setAddress(address);

            boolean success = patientDAO.updatePatient(patient);

            if (success) {
                response.sendRedirect(request.getContextPath() + "/patients");
            } else {
                request.setAttribute("error",
                    "Unable to update patient. Check the patient ID and database.");
                request.setAttribute("patient", patient);
                request.getRequestDispatcher("/edit-patient.jsp")
                       .forward(request, response);
            }

        } catch (NumberFormatException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid patient ID or age."
            );
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Enter a valid name and age.");
            request.getRequestDispatcher("/edit-patient.jsp")
                   .forward(request, response);
        }
    }
}
