<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ page import="com.medical.util.View" %>
<%
    String ctx = request.getContextPath();

    Object statusValue = request.getAttribute("jakarta.servlet.error.status_code");
    int status = statusValue instanceof Integer ? (Integer) statusValue : 500;

    Object messageValue = request.getAttribute("jakarta.servlet.error.message");

    String message = status < 500 && messageValue != null
            ? messageValue.toString() : null;

    if (message == null || message.isBlank()) {
        message = switch (status) {
            case 400 -> "The request could not be processed. Check the details and try again.";
            case 403 -> "This action is not allowed. Reload the page and try again.";
            case 404 -> "The page or record you requested could not be found.";
            default -> "Something went wrong while processing your request.";
        };
    }

    String heading = switch (status) {
        case 400 -> "Bad Request";
        case 403 -> "Forbidden";
        case 404 -> "Not Found";
        case 503 -> "Service Unavailable";
        default -> "Unexpected Error";
    };
%>
<!DOCTYPE html>
<html lang="en">
<head>
<%
    String cspNonce = (String) request.getAttribute("cspNonce");
%>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= status %> - Medical Assist</title>
    <link rel="stylesheet" href="<%= ctx %>/css/app.css">
    <style nonce="<%= View.esc(cspNonce) %>">
        .actions { justify-content: center; }
    </style>
</head>
<body>

<div class="error-page">
    <div class="error-card">
        <h1><%= status %></h1>
        <h2><%= View.esc(heading) %></h2>
        <p><%= View.esc(message) %></p>

        <div class="actions error-actions">
            <a class="btn" href="<%= ctx %>/dashboard">Back to Dashboard</a>
            <a class="btn btn-secondary" href="<%= ctx %>/login">Sign In</a>
        </div>
    </div>
</div>

</body>
</html>
