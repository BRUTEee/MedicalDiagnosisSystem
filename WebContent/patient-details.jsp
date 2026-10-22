
<%@ page import="jakarta.servlet.http.HttpServletResponse" %>
<%@ page import="com.medical.model.Patient" %>
<%
    Object user = session.getAttribute("user");

    if (user == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    Patient patient = (Patient) request.getAttribute("patient");

    if (patient == null) {
        response.sendError(
            HttpServletResponse.SC_NOT_FOUND,
            "Patient details are unavailable."
        );
        return;
    }

    String context = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Patient Details | Medical Diagnosis System</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            padding: 32px 16px;
            font-family: Arial, sans-serif;
            background: #f4f7fb;
            color: #1e293b;
        }

        .container {
            max-width: 850px;
            margin: auto;
        }

        .topbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 12px;
            flex-wrap: wrap;
            margin-bottom: 24px;
        }

        .topbar h1 {
            margin: 0;
            font-size: 26px;
        }

        .button {
            display: inline-block;
            padding: 11px 16px;
            border-radius: 8px;
            text-decoration: none;
            font-size: 14px;
            font-weight: bold;
        }

        .back {
            background: #e2e8f0;
            color: #1e293b;
        }

        .card {
            padding: 28px;
            background: white;
            border-radius: 14px;
            box-shadow: 0 5px 22px rgba(15, 23, 42, 0.06);
        }

        .profile-header {
            padding-bottom: 22px;
            border-bottom: 1px solid #e2e8f0;
            margin-bottom: 20px;
        }

        .profile-header h2 {
            margin: 0 0 8px;
            font-size: 23px;
        }

        .profile-header p {
            margin: 0;
            color: #64748b;
        }

        .details {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 20px;
        }

        .field {
            padding: 15px;
            border: 1px solid #e2e8f0;
            border-radius: 9px;
            min-width: 0;
        }

        .label {
            display: block;
            margin-bottom: 8px;
            color: #64748b;
            font-size: 12px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        .value {
            overflow-wrap: anywhere;
            font-size: 15px;
            font-weight: 600;
        }

        @media (max-width: 560px) {
            .details {
                grid-template-columns: 1fr;
            }

            .card {
                padding: 18px;
            }
        }
    </style>
</head>
<body>
    <main class="container">

        <div class="topbar">
            <h1>Patient Details</h1>

            <a class="button back"
               href="<%= context %>/patients">
                &larr; Back to Patients
            </a>
        </div>

        <section class="card">

            <div class="profile-header">
                <h2><%= escapeHtml(patient.getName()) %></h2>
                <p>Patient ID: <%= patient.getId() %></p>
            </div>

            <div class="details">

                <div class="field">
                    <span class="label">Age</span>
                    <div class="value"><%= patient.getAge() %> years</div>
                </div>

                <div class="field">
                    <span class="label">Gender</span>
                    <div class="value"><%= escapeHtml(patient.getGender()) %></div>
                </div>

                <div class="field">
                    <span class="label">Blood Group</span>
                    <div class="value"><%= escapeHtml(patient.getBloodGroup()) %></div>
                </div>

                <div class="field">
                    <span class="label">Phone</span>
                    <div class="value"><%= escapeHtml(patient.getPhone()) %></div>
                </div>

                <div class="field">
                    <span class="label">Email</span>
                    <div class="value"><%= escapeHtml(patient.getEmail()) %></div>
                </div>

                <div class="field">
                    <span class="label">Address</span>
                    <div class="value"><%= escapeHtml(patient.getAddress()) %></div>
                </div>

            </div>
        </section>
    </main>
</body>
</html>

<%!
    private String escapeHtml(String value) {
        if (value == null) {
            return "Not provided";
        }

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }
%>
