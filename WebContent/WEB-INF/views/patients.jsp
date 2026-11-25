<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.medical.model.Patient" %>
<%@ page import="com.medical.util.View" %>
<%
    request.setAttribute("pageTitle", "Patients");
    request.setAttribute("pageSubtitle", "Search, register and manage patient records.");
    request.setAttribute("activePage", "patients");

    List<Patient> patients = (List<Patient>) request.getAttribute("patients");
    String query = (String) request.getAttribute("query");

    if (query == null) {
        query = "";
    }
%>
<%@ include file="partials/header.jsp" %>

<div class="card">

    <div class="card-header">
        <h2>Patient Records</h2>
        <a class="btn" href="<%= ctx %>/patients?action=new">Register Patient</a>
    </div>

    <form method="get" action="<%= ctx %>/patients" class="actions search-form">
        <input type="text" name="q" value="<%= View.esc(query) %>"
                placeholder="Search by name, phone or email" maxlength="100">
        <button type="submit" class="btn btn-secondary">Search</button>
<%
    if (!query.isEmpty()) {
%>
        <a class="btn btn-secondary" href="<%= ctx %>/patients">Clear</a>
<%
    }
%>
    </form>

<%
    if (patients == null || patients.isEmpty()) {
%>
    <p class="empty">
<%= query.isEmpty()
        ? "No patients registered yet."
        : "No patients match \"" + View.esc(query) + "\"." %>
    </p>
<%
    } else {
%>
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Age</th>
                    <th>Gender</th>
                    <th>Blood Group</th>
                    <th>Phone</th>
                    <th>Registered</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
<%
        for (Patient patient : patients) {
%>
                <tr>
                    <td><%= patient.getId() %></td>
                    <td>
                        <a href="<%= ctx %>/patient-details?id=<%= patient.getId() %>">
                            <%= View.esc(patient.getName()) %>
                        </a>
                    </td>
                    <td><%= patient.getAge() %></td>
                    <td><%= View.text(patient.getGender()) %></td>
                    <td><%= View.text(patient.getBloodGroup()) %></td>
                    <td class="nowrap"><%= View.text(patient.getPhone()) %></td>
                    <td class="nowrap"><%= View.date(patient.getCreatedAt()) %></td>
                    <td class="nowrap">
                        <a href="<%= ctx %>/patients?action=edit&amp;id=<%= patient.getId() %>">Edit</a>
                    </td>
                </tr>
<%
        }
%>
            </tbody>
        </table>
    </div>
<%
    }
%>

</div>

<%@ include file="partials/footer.jsp" %>
