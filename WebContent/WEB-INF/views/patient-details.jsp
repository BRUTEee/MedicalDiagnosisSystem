<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.medical.model.Appointment" %>
<%@ page import="com.medical.model.Diagnosis" %>
<%@ page import="com.medical.model.Patient" %>
<%@ page import="com.medical.util.Csrf" %>
<%@ page import="com.medical.util.View" %>
<%
    Patient patient = (Patient) request.getAttribute("patient");

    List<Diagnosis> assessments = (List<Diagnosis>) request.getAttribute("assessments");
    List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");

    boolean hasAssessments = assessments != null && !assessments.isEmpty();
    boolean appointmentsAvailable =
            Boolean.TRUE.equals(request.getAttribute("appointmentsAvailable"));
    boolean hasAppointments = appointments != null && !appointments.isEmpty();

    request.setAttribute("pageTitle", "Patient Details");
    request.setAttribute("pageSubtitle", patient.getName());
    request.setAttribute("activePage", "patients");
%>
<%@ include file="partials/header.jsp" %>

<div class="card">

    <div class="card-header">
        <h2>Patient Record</h2>

        <div class="actions">
            <a class="btn btn-secondary btn-small"
               href="<%= ctx %>/patients?action=edit&amp;id=<%= patient.getId() %>">Edit</a>

            <a class="btn btn-small"
               href="<%= ctx %>/diagnosis?patientId=<%= patient.getId() %>">New Assessment</a>
<%
    if (appointmentsAvailable) {
%>
            <a class="btn btn-small"
               href="<%= ctx %>/appointments?patientId=<%= patient.getId() %>&amp;returnToPatient=true">Schedule Appointment</a>
<%
    }
%>

<%
    if (!hasAssessments && (!appointmentsAvailable || !hasAppointments)) {
%>
            <form method="post" action="<%= ctx %>/patients" class="inline-form"
                  data-confirm="Delete this patient? This cannot be undone.">
                <%= Csrf.field(request) %>
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" value="<%= patient.getId() %>">
                <button type="submit" class="btn btn-danger btn-small">Delete</button>
            </form>
<%
    }
%>
        </div>
    </div>

    <dl class="detail-list">
        <div>
            <dt>Full Name</dt>
            <dd><%= View.esc(patient.getName()) %></dd>
        </div>
        <div>
            <dt>Age</dt>
            <dd><%= patient.getAge() %></dd>
        </div>
        <div>
            <dt>Gender</dt>
            <dd><%= View.text(patient.getGender()) %></dd>
        </div>
        <div>
            <dt>Blood Group</dt>
            <dd><%= View.text(patient.getBloodGroup()) %></dd>
        </div>
        <div>
            <dt>Phone</dt>
            <dd><%= View.text(patient.getPhone()) %></dd>
        </div>
        <div>
            <dt>Email</dt>
            <dd><%= View.text(patient.getEmail()) %></dd>
        </div>
        <div class="full">
            <dt>Address</dt>
            <dd><%= View.text(patient.getAddress()) %></dd>
        </div>
        <div>
            <dt>Registered</dt>
            <dd><%= View.dateTime(patient.getCreatedAt()) %></dd>
        </div>
    </dl>

</div>

<div class="card">
    <h2>Assessment History</h2>

<%
    if (!hasAssessments) {
%>
    <p class="empty">No assessments recorded for this patient.</p>
<%
    } else {
%>
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
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

            if (symptoms.length() > 100) {
                symptoms = symptoms.substring(0, 100) + "...";
            }
%>
                <tr>
                    <td><%= diagnosis.getId() %></td>
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
    <h2>Appointments</h2>

<%
    if (!appointmentsAvailable) {
%>
    <p class="empty">Appointment scheduling is not available in this database yet.</p>
<%
    } else if (appointments == null || appointments.isEmpty()) {
%>
    <p class="empty">No appointments scheduled for this patient.</p>
<%
    } else {
%>
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>Scheduled</th>
                    <th>Reason</th>
                    <th>Status</th>
                </tr>
            </thead>
            <tbody>
<%
        for (Appointment appointment : appointments) {
%>
                <tr>
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
