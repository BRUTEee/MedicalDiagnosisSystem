<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.medical.model.Appointment" %>
<%@ page import="com.medical.model.Patient" %>
<%@ page import="com.medical.util.Csrf" %>
<%@ page import="com.medical.util.View" %>
<%
    boolean available = Boolean.TRUE.equals(request.getAttribute("appointmentsAvailable"));

    List<Appointment> appointments = (List<Appointment>) request.getAttribute("appointments");
    List<Patient> patients = (List<Patient>) request.getAttribute("patients");

    String status = (String) request.getAttribute("status");
    String error = (String) request.getAttribute("error");
    boolean returnToPatient = Boolean.TRUE.equals(
            request.getAttribute("returnToPatient"));

    String formReason = (String) request.getAttribute("formReason");
    String formScheduledAt = (String) request.getAttribute("formScheduledAt");

    Object selectedValue = request.getAttribute("selectedPatientId");
    int selectedPatientId = selectedValue instanceof Integer ? (Integer) selectedValue : 0;

    boolean hasPatients = patients != null && !patients.isEmpty();

    request.setAttribute("pageTitle", "Appointments");
    request.setAttribute("pageSubtitle", "Schedule and track patient appointments.");
    request.setAttribute("activePage", "appointments");

    String[][] filters = new String[][] {
        { null, "All" },
        { Appointment.SCHEDULED, "Scheduled" },
        { Appointment.COMPLETED, "Completed" },
        { Appointment.CANCELLED, "Cancelled" }
    };
%>
<%@ include file="partials/header.jsp" %>

<%
    if (!available) {
%>
<div class="alert alert-info">
    Appointment scheduling is not available because the <code>appointments</code>
    table has not been created in the database yet. Run the schema migration and
    reload this page.
</div>
<%
    } else {
%>

<%
    if (error != null && !error.isEmpty()) {
%>
<div class="alert alert-error"><%= View.esc(error) %></div>
<%
    }
%>

<div class="card">

    <h2>Schedule an Appointment</h2>

<%
    if (!hasPatients) {
%>
    <p class="empty">
        No patients are registered yet.
        <a href="<%= ctx %>/patients?action=new">Register a patient</a> first.
    </p>
<%
    } else {
%>
    <form method="post" action="<%= ctx %>/appointments">

        <%= Csrf.field(request) %>
        <input type="hidden" name="action" value="create">
<%
        if (returnToPatient && selectedPatientId > 0) {
%>
        <input type="hidden" name="returnToPatient" value="true">
<%
        }
%>

        <div class="form-grid">

            <div class="form-group">
                <label for="patientId">Patient</label>
                <select id="patientId" name="patientId" required>
                    <option value="">Select a patient</option>
<%
        for (Patient patient : patients) {
%>
                    <option value="<%= patient.getId() %>"<%= patient.getId() == selectedPatientId ? " selected" : "" %>>
                        <%= View.esc(patient.getName()) %> (ID <%= patient.getId() %>)
                    </option>
<%
        }
%>
                </select>
            </div>

            <div class="form-group">
                <label for="scheduledAt">Date and Time</label>
                <input type="datetime-local" id="scheduledAt" name="scheduledAt" required
                       value="<%= View.esc(formScheduledAt) %>">
            </div>

            <div class="form-group full">
                <label for="reason">Reason (optional)</label>
                <input type="text" id="reason" name="reason" maxlength="255"
                       value="<%= View.esc(formReason) %>">
            </div>

        </div>

        <div class="form-footer">
            <button type="submit" class="btn">Schedule Appointment</button>
        </div>

    </form>
<%
    }
%>

</div>

<div class="card">

    <h2>Appointments</h2>

    <div class="filters">
<%
    for (String[] filter : filters) {
        boolean active = filter[0] == null
                ? status == null
                : filter[0].equals(status);

        String href = filter[0] == null
                ? ctx + "/appointments"
                : ctx + "/appointments?status=" + filter[0];
%>
        <a href="<%= href %>"<%= active ? " class=\"active\"" : "" %>><%= filter[1] %></a>
<%
    }
%>
    </div>

<%
    if (appointments == null || appointments.isEmpty()) {
%>
    <p class="empty">No appointments to show.</p>
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
                    <th></th>
                </tr>
            </thead>
            <tbody>
<%
        for (Appointment appointment : appointments) {
            boolean scheduled = Appointment.SCHEDULED.equals(appointment.getStatus());
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
                    <td class="nowrap">
<%
            if (scheduled) {
%>
                        <form method="post" action="<%= ctx %>/appointments" class="inline-form">
                            <%= Csrf.field(request) %>
                            <input type="hidden" name="action" value="complete">
                            <input type="hidden" name="id" value="<%= appointment.getId() %>">
                            <button type="submit" class="btn btn-small">Complete</button>
                        </form>
                        <form method="post" action="<%= ctx %>/appointments" class="inline-form"
                              data-confirm="Cancel this appointment?">
                            <%= Csrf.field(request) %>
                            <input type="hidden" name="action" value="cancel">
                            <input type="hidden" name="id" value="<%= appointment.getId() %>">
                            <button type="submit" class="btn btn-danger btn-small">Cancel</button>
                        </form>
<%
            } else {
%>
                        <span class="muted">&mdash;</span>
<%
            }
%>
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

<%
    }
%>

<%@ include file="partials/footer.jsp" %>
