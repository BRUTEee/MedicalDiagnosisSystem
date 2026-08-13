package com.medical.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.medical.dao.PatientDAO;
import com.medical.model.Patient;

@WebServlet("/patients")
public class PatientServlet extends HttpServlet {

    private PatientDAO patientDAO;

    @Override
    public void init() {
        patientDAO = new PatientDAO();
    }


    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        List<Patient> patients = patientDAO.getAllPatients();

        request.setAttribute("patients", patients);

        request.getRequestDispatcher("patients.jsp")
               .forward(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        int age = Integer.parseInt(request.getParameter("age"));
        String gender = request.getParameter("gender");
        String bloodGroup = request.getParameter("bloodGroup");
        String phone = request.getParameter("phone");
        String email = request.getParameter("email");
        String address = request.getParameter("address");

        Patient patient = new Patient();

        patient.setName(name);
        patient.setAge(age);
        patient.setGender(gender);
        patient.setBloodGroup(bloodGroup);
        patient.setPhone(phone);
        patient.setEmail(email);
        patient.setAddress(address);

        boolean success = patientDAO.addPatient(patient);

        if (success) {
            response.sendRedirect("patients");
        } else {
            request.setAttribute(
                "error",
                "Unable to add patient."
            );

            request.getRequestDispatcher("add-patient.jsp")
                   .forward(request, response);
        }
    }
}