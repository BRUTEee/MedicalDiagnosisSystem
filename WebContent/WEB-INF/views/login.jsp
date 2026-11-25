<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.medical.util.Csrf" %>
<%@ page import="com.medical.util.View" %>
<%
    String ctx = request.getContextPath();
    String error = (String) request.getAttribute("error");
    String email = (String) request.getAttribute("email");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Doctor Login - Medical Assist</title>
    <link rel="stylesheet" href="<%= ctx %>/css/login.css">
</head>
<body>

<div class="login-container">

    <div class="login-card">

        <div class="logo">MEDICAL <span>ASSIST</span></div>

        <h1>Doctor Login</h1>

        <p class="subtitle">Sign in to access the medical assistance system.</p>

<%
    if (error != null && !error.isEmpty()) {
%>
        <p class="error"><%= View.esc(error) %></p>
<%
    }
%>

        <form action="<%= ctx %>/login" method="post">

            <%= Csrf.field(request) %>

            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email"
                       placeholder="Enter your email"
                       value="<%= View.esc(email) %>"
                       autocomplete="username" required>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password"
                       placeholder="Enter your password"
                       autocomplete="current-password" required>
            </div>

            <button type="submit" class="login-btn">Sign In</button>

        </form>

        <a href="<%= ctx %>/index.jsp" class="back-home">&larr; Back to Home</a>

    </div>

</div>

</body>
</html>
