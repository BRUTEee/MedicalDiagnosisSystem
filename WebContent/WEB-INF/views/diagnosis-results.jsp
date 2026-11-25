<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.medical.model.Diagnosis" %>
<%@ page import="com.medical.model.Patient" %>
<%@ page import="com.medical.util.View" %>
<%
    Diagnosis diagnosis = (Diagnosis) request.getAttribute("diagnosis");
    Patient patient = (Patient) request.getAttribute("patient");

    boolean urgent = Boolean.TRUE.equals(request.getAttribute("urgent"));

    request.setAttribute("pageTitle", "Assessment Record");
    request.setAttribute("pageSubtitle", "Assessment #" + diagnosis.getId());
    request.setAttribute("activePage", "diagnosis");
%>
<%@ include file="partials/header.jsp" %>

<%
    if (urgent) {
%>
<div class="alert alert-error">
    <strong>Warning signs detected.</strong>
    Arrange prompt clinical assessment and use emergency services if the
    symptoms are current or severe.
</div>
<%
    }
%>

<div class="card">

    <div class="card-header">
        <h2>Assessment #<%= diagnosis.getId() %></h2>

        <div class="actions">
            <span class="badge <%= urgent ? "badge-urgent" : "badge-completed" %>">
                <%= urgent ? "Review recommended" : "Recorded" %>
            </span>
            <a class="btn btn-secondary btn-small"
               href="<%= ctx %>/patient-details?id=<%= patient.getId() %>">Patient Record</a>
        </div>
    </div>

    <dl class="detail-list">
        <div>
            <dt>Patient</dt>
            <dd>
                <a href="<%= ctx %>/patient-details?id=<%= patient.getId() %>">
                    <%= View.esc(patient.getName()) %>
                </a>
            </dd>
        </div>
        <div>
            <dt>Recorded</dt>
            <dd><%= View.dateTime(diagnosis.getCreatedAt()) %></dd>
        </div>
        <div>
            <dt>Age / Gender</dt>
            <dd><%= patient.getAge() %> &middot; <%= View.text(patient.getGender()) %></dd>
        </div>
        <div>
            <dt>Blood Group</dt>
            <dd><%= View.text(patient.getBloodGroup()) %></dd>
        </div>
    </dl>

</div>

<div class="card">
    <h2>Reported Symptoms</h2>
    <p class="pre"><%= View.esc(diagnosis.getSymptoms()) %></p>
</div>

<div class="card">
    <h2>Rule-Based Review</h2>
    <p class="pre"><%= View.esc(diagnosis.getPossibleConditions()) %></p>
</div>

<div class="card">
    <h2>Recommendations</h2>
    <p class="pre"><%= View.esc(diagnosis.getRecommendations()) %></p>
</div>

<div class="actions">
    <a class="btn" href="<%= ctx %>/diagnosis?patientId=<%= patient.getId() %>">New Assessment</a>
    <a class="btn btn-secondary" href="<%= ctx %>/patient-details?id=<%= patient.getId() %>">Back to Patient</a>
</div>

<%@ include file="partials/footer.jsp" %>
