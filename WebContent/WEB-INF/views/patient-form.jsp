<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.medical.model.Patient" %>
<%@ page import="com.medical.util.Csrf" %>
<%@ page import="com.medical.util.PatientValidator" %>
<%@ page import="com.medical.util.View" %>
<%
    Patient patient = (Patient) request.getAttribute("patient");

    if (patient == null) {
        patient = new Patient();
    }

    boolean editing = patient.getId() > 0;

    List<String> errors = (List<String>) request.getAttribute("errors");

    request.setAttribute("pageTitle", editing ? "Edit Patient" : "Register Patient");
    request.setAttribute("pageSubtitle", editing
            ? "Update the patient's recorded details."
            : "Add a new patient to the system.");
    request.setAttribute("activePage", "patients");
%>
<%@ include file="partials/header.jsp" %>

<div class="card">

    <h2><%= editing ? "Edit Patient" : "Patient Details" %></h2>

<%
    if (errors != null && !errors.isEmpty()) {
%>
    <div class="alert alert-error">
        <strong>Please correct the following:</strong>
        <ul>
<%
        for (String error : errors) {
%>
            <li><%= View.esc(error) %></li>
<%
        }
%>
        </ul>
    </div>
<%
    }
%>

    <form method="post" action="<%= ctx %>/patients">

        <%= Csrf.field(request) %>
        <input type="hidden" name="action" value="<%= editing ? "update" : "create" %>">

<%
    if (editing) {
%>
        <input type="hidden" name="id" value="<%= patient.getId() %>">
<%
    }
%>

        <div class="form-grid">

            <div class="form-group">
                <label for="name">Full Name</label>
                <input type="text" id="name" name="name" maxlength="100" required
                       value="<%= View.esc(patient.getName()) %>">
            </div>

            <div class="form-group">
                <label for="age">Age</label>
                <input type="number" id="age" name="age" min="1" max="120" required
                       value="<%= patient.getAge() > 0 ? patient.getAge() : "" %>">
            </div>

            <div class="form-group">
                <label for="gender">Gender (optional)</label>
                <select id="gender" name="gender">
                    <option value="">Select gender</option>
<%
    for (String gender : PatientValidator.GENDERS) {
%>
                    <option value="<%= View.esc(gender) %>"<%= View.selected(gender, patient.getGender()) %>>
                        <%= View.esc(gender) %>
                    </option>
<%
    }
%>
                </select>
            </div>

            <div class="form-group">
                <label for="bloodGroup">Blood Group (optional)</label>
                <select id="bloodGroup" name="bloodGroup">
                    <option value="">Select blood group</option>
<%
    for (String group : PatientValidator.BLOOD_GROUPS) {
%>
                    <option value="<%= View.esc(group) %>"<%= View.selected(group, patient.getBloodGroup()) %>>
                        <%= View.esc(group) %>
                    </option>
<%
    }
%>
                </select>
            </div>

            <div class="form-group">
                <label for="phone">Phone</label>
                <input type="tel" id="phone" name="phone" maxlength="20"
                       placeholder="e.g. +94 77 123 4567"
                       value="<%= View.esc(patient.getPhone()) %>">
            </div>

            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" maxlength="100"
                       value="<%= View.esc(patient.getEmail()) %>">
            </div>

            <div class="form-group full">
                <label for="address">Address</label>
                <textarea id="address" name="address" maxlength="255"><%= View.esc(patient.getAddress()) %></textarea>
            </div>

        </div>

        <div class="form-footer">
            <button type="submit" class="btn"><%= editing ? "Save Changes" : "Register Patient" %></button>
<%
    if (editing) {
%>
            <a class="btn btn-secondary"
               href="<%= ctx %>/patient-details?id=<%= patient.getId() %>">Cancel</a>
<%
    } else {
%>
            <a class="btn btn-secondary" href="<%= ctx %>/patients">Cancel</a>
<%
    }
%>
        </div>

    </form>

</div>

<%@ include file="partials/footer.jsp" %>
