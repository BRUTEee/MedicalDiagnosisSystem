<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.medical.model.Diagnosis" %>
<%@ page import="com.medical.util.View" %>
<%
    List<Diagnosis> assessments = (List<Diagnosis>) request.getAttribute("assessments");
    Map<String, Integer> genderCounts = (Map<String, Integer>) request.getAttribute("genderCounts");
    Map<String, Integer> bloodGroupCounts = (Map<String, Integer>) request.getAttribute("bloodGroupCounts");

    int patientCount = (Integer) request.getAttribute("patientCount");

    int assessmentCount = assessments == null ? 0 : assessments.size();

    request.setAttribute("pageTitle", "Reports");
    request.setAttribute("pageSubtitle", "Summary statistics and assessment records.");
    request.setAttribute("activePage", "reports");
%>
<%@ include file="partials/header.jsp" %>

<div class="stats">

    <div class="stat-card">
        <h3>Total Patients</h3>
        <div class="number"><%= patientCount %></div>
    </div>

    <div class="stat-card">
        <h3>Total Assessments</h3>
        <div class="number"><%= assessmentCount %></div>
    </div>

    <div class="stat-card">
        <h3>Gender Groups</h3>
        <div class="number"><%= genderCounts == null ? 0 : genderCounts.size() %></div>
    </div>

    <div class="stat-card">
        <h3>Blood Groups</h3>
        <div class="number"><%= bloodGroupCounts == null ? 0 : bloodGroupCounts.size() %></div>
    </div>

</div>

<div class="card">
    <h2>Patients by Gender</h2>

<%
    if (genderCounts == null || genderCounts.isEmpty()) {
%>
    <p class="empty">No patient data available.</p>
<%
    } else {
%>
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>Gender</th>
                    <th>Patients</th>
                </tr>
            </thead>
            <tbody>
<%
        for (Map.Entry<String, Integer> entry : genderCounts.entrySet()) {
%>
                <tr>
                    <td><%= View.esc(entry.getKey()) %></td>
                    <td><%= entry.getValue() %></td>
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

<div class="card">
    <h2>Patients by Blood Group</h2>

<%
    if (bloodGroupCounts == null || bloodGroupCounts.isEmpty()) {
%>
    <p class="empty">No patient data available.</p>
<%
    } else {
%>
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>Blood Group</th>
                    <th>Patients</th>
                </tr>
            </thead>
            <tbody>
<%
        for (Map.Entry<String, Integer> entry : bloodGroupCounts.entrySet()) {
%>
                <tr>
                    <td><%= View.esc(entry.getKey()) %></td>
                    <td><%= entry.getValue() %></td>
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

<div class="card">

    <div class="card-header">
        <h2>Assessment Records</h2>
        <a class="btn btn-secondary btn-small" href="<%= ctx %>/reports?format=csv">Export CSV</a>
    </div>

<%
    if (assessments == null || assessments.isEmpty()) {
%>
    <p class="empty">No assessments recorded yet.</p>
<%
    } else {
%>
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Patient</th>
                    <th>Recorded</th>
                    <th>Reported Symptoms</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
<%
        for (Diagnosis diagnosis : assessments) {
            String symptoms = diagnosis.getSymptoms() == null
                    ? "" : diagnosis.getSymptoms().trim();

            if (symptoms.length() > 80) {
                symptoms = symptoms.substring(0, 80) + "...";
            }
%>
                <tr>
                    <td><%= diagnosis.getId() %></td>
                    <td>
                        <a href="<%= ctx %>/patient-details?id=<%= diagnosis.getPatientId() %>">
                            <%= View.esc(diagnosis.getPatientName()) %>
                        </a>
                    </td>
                    <td class="nowrap"><%= View.dateTime(diagnosis.getCreatedAt()) %></td>
                    <td><%= View.esc(symptoms) %></td>
                    <td><a href="<%= ctx %>/diagnosis?id=<%= diagnosis.getId() %>">View</a></td>
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
