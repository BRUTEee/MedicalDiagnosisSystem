<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.medical.util.Csrf" %>
<%@ page import="com.medical.util.View" %>
<%
    String ctx = request.getContextPath();
    String error = (String) request.getAttribute("error");
    String configurationMessage =
            (String) request.getAttribute("configurationMessage");
    String patientId = (String) request.getAttribute("patientId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient Portal Sign In</title>
    <link rel="stylesheet" href="<%= ctx %>/css/login.css">
</head>
<body>
<div class="login-container">
    <div class="login-card">
        <div class="logo">MEDICAL <span>ASSIST</span></div>
        <h1>Patient Portal</h1>
<%
    if (configurationMessage != null) {
%>
        <p class="error"><%= View.esc(configurationMessage) %></p>
<%
    }
    if (error != null && !error.isEmpty()) {
%>
        <p class="error"><%= View.esc(error) %></p>
<%
    }
%>
        <form action="<%= ctx %>/patient-login" method="post">
            <%= Csrf.field(request) %>
            <div class="form-group">
                <label for="patientId">Patient ID</label>
                <input type="text" id="patientId" name="patientId"
                       value="<%= View.esc(patientId) %>" autocomplete="username" required>
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password"
                       autocomplete="current-password" required>
            </div>
            <button type="submit" class="login-btn">Sign In</button>
        </form>
        <a href="<%= ctx %>/index.jsp" class="back-home">&larr; Back to Home</a>
    </div>
</div>
</body>
</html>