<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.medical.model.Appointment" %>
<%@ page import="com.medical.model.Diagnosis" %>
<%@ page import="com.medical.util.View" %>
<%
    request.setAttribute("pageTitle", "Dashboard");
    request.setAttribute("activePage", "dashboard");

    int patientCount = (Integer) request.getAttribute("patientCount");
    int assessmentCount = (Integer) request.getAttribute("assessmentCount");
    int assessmentsToday = (Integer) request.getAttribute("assessmentsToday");
    int upcomingCount = (Integer) request.getAttribute("upcomingCount");

    boolean appointmentsAvailable =
            Boolean.TRUE.equals(request.getAttribute("appointmentsAvailable"));

    List<Diagnosis> recentAssessments =
            (List<Diagnosis>) request.getAttribute("recentAssessments");

    List<Appointment> upcomingAppointments =
            (List<Appointment>) request.getAttribute("upcomingAppointments");
%>
<%@ include file="partials/header.jsp" %>

<div class="stats">

    <div class="stat-card">
        <h3>Total Patients</h3>
        <div class="number"><%= patientCount %></div>
    </div>

    <div class="stat-card">
        <h3>Assessments</h3>
        <div class="number"><%= assessmentCount %></div>
    </div>

    <div class="stat-card">
        <h3>Assessments Today</h3>
        <div class="number"><%= assessmentsToday %></div>
    </div>

    <div class="stat-card">
        <h3>Upcoming Appointments</h3>
        <div class="number"><%= upcomingCount %></div>
    </div>

</div>

<div class="card">
    <h2>Quick Actions</h2>

    <div class="actions">
        <a class="btn" href="<%= ctx %>/patients?action=new">Register Patient</a>
        <a class="btn btn-secondary" href="<%= ctx %>/patients">Manage Patients</a>
        <a class="btn btn-secondary" href="<%= ctx %>/diagnosis">New Assessment</a>
        <a class="btn btn-secondary" href="<%= ctx %>/appointments">Appointments</a>
    </div>
</div>

<div class="card">
    <div class="card-header">
        <h2>Recent Assessments</h2>
        <a class="btn btn-secondary btn-small" href="<%= ctx %>/reports">View all</a>
    </div>

<%
    if (recentAssessments == null || recentAssessments.isEmpty()) {
%>
    <p class="empty">No assessments recorded yet.</p>
<%
    } else {
%>
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>Patient</th>
                    <th>Recorded</th>
                    <th>Reported Symptoms</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
<%
        for (Diagnosis diagnosis : recentAssessments) {
            String symptoms = diagnosis.getSymptoms() == null
                    ? "" : diagnosis.getSymptoms().trim();

            if (symptoms.length() > 90) {
                symptoms = symptoms.substring(0, 90) + "...";
            }
%>
                <tr>
                    <td><%= View.esc(diagnosis.getPatientName()) %></td>
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

<div class="card">
    <div class="card-header">
        <h2>Upcoming Appointments</h2>
        <a class="btn btn-secondary btn-small" href="<%= ctx %>/appointments">View all</a>
    </div>

<%
    if (!appointmentsAvailable) {
%>
    <p class="empty">Appointment scheduling is not set up in this database yet.</p>
<%
    } else if (upcomingAppointments == null || upcomingAppointments.isEmpty()) {
%>
    <p class="empty">No upcoming appointments.</p>
<%
    } else {
%>
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>Patient</th>
                    <th>Scheduled</th>
                    <th>Reason</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
<%
        for (Appointment appointment : upcomingAppointments) {
%>
                <tr>
                    <td>
                        <a href="<%= ctx %>/patient-details?id=<%= appointment.getPatientId() %>">
                            <%= View.esc(appointment.getPatientName()) %>
                        </a>
                    </td>
                    <td class="nowrap"><%= View.dateTime(appointment.getScheduledAt()) %></td>
                    <td><%= View.text(appointment.getReason()) %></td>
                    <td>
                        <span class="badge badge-<%= View.esc(appointment.getStatus() == null
                                ? "" : appointment.getStatus().toLowerCase(java.util.Locale.ROOT)) %>">
                            <%= View.esc(appointment.getStatus()) %>
                        </span>
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
