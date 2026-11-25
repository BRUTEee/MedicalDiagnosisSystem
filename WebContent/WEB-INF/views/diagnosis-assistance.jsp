<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.medical.model.Patient" %>
<%@ page import="com.medical.util.Csrf" %>
<%@ page import="com.medical.util.View" %>
<%
    List<Patient> patients = (List<Patient>) request.getAttribute("patients");

    String error = (String) request.getAttribute("error");
    String symptoms = (String) request.getAttribute("symptoms");

    Object selectedValue = request.getAttribute("selectedPatientId");
    int selectedPatientId = selectedValue instanceof Integer ? (Integer) selectedValue : 0;

    boolean hasPatients = patients != null && !patients.isEmpty();

    request.setAttribute("pageTitle", "Symptom Assessment");
    request.setAttribute("pageSubtitle",
            "Record reported symptoms and generate a rule-based review.");
    request.setAttribute("activePage", "diagnosis");
%>
<%@ include file="partials/header.jsp" %>

<div class="alert alert-warning">
    <strong>Clinical safety notice:</strong>
    The rule-based review provided here is an educational aid, not a diagnosis.
    A qualified healthcare professional must evaluate the patient and make all
    clinical decisions.
</div>

<div class="card">

    <h2>New Assessment</h2>

<%
    if (error != null && !error.isEmpty()) {
%>
    <div class="alert alert-error"><%= View.esc(error) %></div>
<%
    }

    if (!hasPatients) {
%>
    <p class="empty">
        No patients are registered yet.
        <a href="<%= ctx %>/patients?action=new">Register a patient</a> first.
    </p>
<%
    } else {
%>
    <form method="post" action="<%= ctx %>/diagnosis">

        <%= Csrf.field(request) %>

        <div class="form-grid">

            <div class="form-group full">
                <label for="patientId">Patient</label>
                <select id="patientId" name="patientId" required>
                    <option value="">Select a patient</option>
<%
        for (Patient patient : patients) {
            boolean selected = patient.getId() == selectedPatientId;
%>
                    <option value="<%= patient.getId() %>"<%= selected ? " selected" : "" %>>
                        <%= View.esc(patient.getName()) %>
                        (ID <%= patient.getId() %>, age <%= patient.getAge() %>)
                    </option>
<%
        }
%>
                </select>
            </div>

            <div class="form-group full">
                <label for="symptoms">Reported Symptoms</label>
                <textarea id="symptoms" name="symptoms" maxlength="5000" required
                          placeholder="Describe the symptoms, their duration and severity..."><%= View.esc(symptoms) %></textarea>
            </div>

        </div>

        <div class="form-footer">
            <button type="submit" class="btn">Run Review</button>
        </div>

    </form>
<%
    }
%>

</div>

<%@ include file="partials/footer.jsp" %>
