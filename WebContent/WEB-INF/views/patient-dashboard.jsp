<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.medical.model.PatientPortalSession" %>
<%@ page import="com.medical.model.Patient" %>
<%@ page import="com.medical.model.Appointment" %>
<%@ page import="com.medical.model.Diagnosis" %>
<%@ page import="java.util.List" %>
<%@ page import="com.medical.util.Csrf" %>
<%@ page import="com.medical.util.View" %>
<%
    PatientPortalSession portalSession =
            (PatientPortalSession) request.getAttribute("patientPortal");
    Patient patient = (Patient) request.getAttribute("patient");
    List<Appointment> upcomingAppointments = (List<Appointment>) request.getAttribute("upcomingAppointments");
    List<Appointment> pastAppointments = (List<Appointment>) request.getAttribute("pastAppointments");
    List<Diagnosis> assessments = (List<Diagnosis>) request.getAttribute("assessments");
    String ctx = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient Dashboard - Medical Assist</title>
    <link rel="stylesheet" href="<%= ctx %>/css/app.css">
    <style>
        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
            gap: 24px;
            margin-top: 24px;
        }
        .dashboard-card {
            background: white;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
            padding: 24px;
        }
        .dashboard-card h3 {
            margin: 0 0 16px;
            font-size: 1.1rem;
            color: #172554;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .dashboard-card h3::before {
            content: "";
            width: 4px;
            height: 20px;
            background: #2563eb;
            border-radius: 2px;
        }
        .patient-header {
            background: linear-gradient(135deg, #2563eb 0%, #1e40af 100%);
            color: white;
            border-radius: 16px;
            padding: 32px;
            margin-bottom: 24px;
        }
        .patient-header h1 {
            margin: 0 0 8px;
            font-size: 1.75rem;
        }
        .patient-header .meta {
            opacity: 0.9;
            font-size: 0.95rem;
        }
        .info-row {
            display: flex;
            justify-content: space-between;
            padding: 10px 0;
            border-bottom: 1px solid #f1f5f9;
        }
        .info-row:last-child {
            border-bottom: none;
        }
        .info-label {
            color: #64748b;
            font-size: 0.9rem;
        }
        .info-value {
            font-weight: 500;
            color: #172554;
        }
        .badge {
            display: inline-flex;
            align-items: center;
            padding: 4px 10px;
            border-radius: 9999px;
            font-size: 0.75rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.025em;
        }
        .badge-scheduled { background: #dbeafe; color: #1e40af; }
        .badge-completed { background: #dcfce7; color: #166534; }
        .badge-cancelled { background: #fee2e2; color: #991b1b; }
        .appointment-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 14px 0;
            border-bottom: 1px solid #f1f5f9;
        }
        .appointment-row:last-child {
            border-bottom: none;
        }
        .appointment-info {
            flex: 1;
        }
        .appointment-date {
            font-weight: 600;
            color: #172554;
            margin-bottom: 4px;
        }
        .appointment-reason {
            color: #64748b;
            font-size: 0.9rem;
        }
        .assessment-row {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            padding: 14px 0;
            border-bottom: 1px solid #f1f5f9;
        }
        .assessment-row:last-child {
            border-bottom: none;
        }
        .assessment-info {
            flex: 1;
            max-width: 70%;
        }
        .assessment-date {
            font-weight: 600;
            color: #172554;
            margin-bottom: 4px;
        }
        .assessment-symptoms {
            color: #64748b;
            font-size: 0.9rem;
            line-height: 1.5;
        }
        .empty-state {
            text-align: center;
            padding: 32px;
            color: #94a3b8;
        }
        .empty-state svg {
            width: 48px;
            height: 48px;
            margin-bottom: 12px;
            opacity: 0.5;
        }
        .btn-sm {
            padding: 8px 16px;
            font-size: 0.85rem;
        }
        .section-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 16px;
        }
        @media (max-width: 768px) {
            .dashboard-grid {
                grid-template-columns: 1fr;
            }
            .appointment-row, .assessment-row {
                flex-direction: column;
                align-items: flex-start;
                gap: 8px;
            }
        }
    </style>
</head>
<body>
<div class="app">
    <main class="main">
        <div class="topbar">
            <div>
                <h1>Patient Dashboard</h1>
                <p>Welcome back, <%= View.esc(patient.getName()) %></p>
            </div>
            <form method="post" action="<%= ctx %>/patient-logout">
                <%= Csrf.field(request) %>
                <button type="submit" class="btn btn-secondary">Sign out</button>
            </form>
        </div>

        <div class="page">
            <div class="patient-header">
                <h1><%= View.esc(patient.getName()) %></h1>
                <div class="meta">
                    Patient ID: <strong><%= patient.getId() %></strong> •
                    Age: <strong><%= patient.getAge() %></strong> •
                    <% if (patient.getGender() != null) { %>
                    Gender: <strong><%= View.esc(patient.getGender()) %></strong> •
                    <% } %>
                    <% if (patient.getBloodGroup() != null) { %>
                    Blood Group: <strong><%= View.esc(patient.getBloodGroup()) %></strong>
                    <% } %>
                </div>
            </div>

            <div class="dashboard-grid">
                <div class="dashboard-card">
                    <h3>Contact Information</h3>
                    <div class="info-row">
                        <span class="info-label">Phone</span>
                        <span class="info-value"><%= View.text(patient.getPhone()) %></span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Email</span>
                        <span class="info-value"><%= View.text(patient.getEmail()) %></span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Address</span>
                        <span class="info-value"><%= View.text(patient.getAddress()) %></span>
                    </div>
                    <div class="info-row">
                        <span class="info-label">Registered</span>
                        <span class="info-value"><%= View.date(patient.getCreatedAt()) %></span>
                    </div>
                </div>

                <div class="dashboard-card">
                    <div class="section-header">
                        <h3>Upcoming Appointments</h3>
                        <a href="<%= ctx %>/appointments?patientId=<%= patient.getId() %>&returnToPatient=true" class="btn btn-secondary btn-sm">View All</a>
                    </div>
                    <%
                        if (upcomingAppointments == null || upcomingAppointments.isEmpty()) {
                    %>
                    <div class="empty-state">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        <p>No upcoming appointments</p>
                        <a href="<%= ctx %>/appointments?patientId=<%= patient.getId() %>&returnToPatient=true" class="btn btn-sm" style="margin-top: 12px; display: inline-block;">Schedule Appointment</a>
                    </div>
                    <%
                        } else {
                            for (Appointment appt : upcomingAppointments) {
                    %>
                    <div class="appointment-row">
                        <div class="appointment-info">
                            <div class="appointment-date">
                                <%= View.dateTime(appt.getScheduledAt()) %>
                                <span class="badge badge-scheduled"><%= View.esc(appt.getStatus()) %></span>
                            </div>
                            <div class="appointment-reason"><%= View.text(appt.getReason()) %></div>
                        </div>
                    </div>
                    <%
                            }
                        }
                    %>
                </div>

                <div class="dashboard-card">
                    <div class="section-header">
                        <h3>Past Appointments</h3>
                    </div>
                    <%
                        if (pastAppointments == null || pastAppointments.isEmpty()) {
                    %>
                    <div class="empty-state">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                        </svg>
                        <p>No past appointments</p>
                    </div>
                    <%
                        } else {
                            for (Appointment appt : pastAppointments) {
                    %>
                    <div class="appointment-row">
                        <div class="appointment-info">
                            <div class="appointment-date">
                                <%= View.dateTime(appt.getScheduledAt()) %>
                                <span class="badge badge-<%= View.esc(appt.getStatus().toLowerCase()) %>"><%= View.esc(appt.getStatus()) %></span>
                            </div>
                            <div class="appointment-reason"><%= View.text(appt.getReason()) %></div>
                        </div>
                    </div>
                    <%
                            }
                        }
                    %>
                </div>

                <div class="dashboard-card" style="grid-column: 1 / -1;">
                    <div class="section-header">
                        <h3>Assessment History</h3>
                        <a href="<%= ctx %>/diagnosis?patientId=<%= patient.getId() %>" class="btn btn-secondary btn-sm">New Assessment</a>
                    </div>
                    <%
                        if (assessments == null || assessments.isEmpty()) {
                    %>
                    <div class="empty-state">
                        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                            <path d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                            <path d="M9 12l2 2 4-4" />
                        </svg>
                        <p>No assessments recorded yet</p>
                        <a href="<%= ctx %>/diagnosis?patientId=<%= patient.getId() %>" class="btn btn-sm" style="margin-top: 12px; display: inline-block;">Create Assessment</a>
                    </div>
                    <%
                        } else {
                            for (Diagnosis d : assessments) {
                                String symptoms = d.getSymptoms() == null ? "" : d.getSymptoms().trim();
                                if (symptoms.length() > 150) {
                                    symptoms = symptoms.substring(0, 150) + "...";
                                }
                    %>
                    <div class="assessment-row">
                        <div class="assessment-info">
                            <div class="assessment-date"><%= View.dateTime(d.getCreatedAt()) %></div>
                            <div class="assessment-symptoms"><%= View.esc(symptoms) %></div>
                        </div>
                        <a href="<%= ctx %>/diagnosis?id=<%= d.getId() %>" class="btn btn-secondary btn-sm" style="flex-shrink: 0;">View Details</a>
                    </div>
                    <%
                            }
                        }
                    %>
                </div>
            </div>
        </div>
    </main>
</div>
</body>
</html>