
<%@ page import="com.medical.model.Patient" %>
<%@ page import="jakarta.servlet.http.HttpServletResponse" %>

<%
    Object loggedInUser = session.getAttribute("user");

    if (loggedInUser == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    Patient patient = (Patient) request.getAttribute("patient");

    if (patient == null) {
        response.sendError(
            HttpServletResponse.SC_NOT_FOUND,
            "Patient details not found. Open Edit from the patient list."
        );
        return;
    }

    String context = request.getContextPath();
%>

<%!
    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
    }
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Edit Patient | Medical Diagnosis System</title>

    <link rel="stylesheet" href="<%= context %>/css/dashboard.css">

    <style>
        * {
            box-sizing: border-box;
        }

        .edit-container {
            max-width: 900px;
            margin: 0 auto;
        }

        .edit-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 15px;
            flex-wrap: wrap;
            margin-bottom: 24px;
        }

        .edit-header h1 {
            margin: 0 0 8px;
        }

        .edit-header p {
            margin: 0;
            color: #64748b;
        }

        .form-card {
            background: #fff;
            padding: 30px;
            border-radius: 14px;
            box-shadow: 0 3px 15px rgba(0, 0, 0, 0.06);
        }

        .patient-id {
            display: inline-block;
            margin-bottom: 24px;
            padding: 7px 12px;
            border-radius: 7px;
            background: #eff6ff;
            color: #1d4ed8;
            font-size: 13px;
            font-weight: bold;
        }

        .form-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 22px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
            gap: 8px;
        }

        .form-group.full-width {
            grid-column: 1 / -1;
        }

        .form-group label {
            font-size: 14px;
            font-weight: 600;
            color: #334155;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 12px 13px;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
            font: inherit;
            color: #0f172a;
            background: #fff;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #2563eb;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.12);
        }

        .form-group textarea {
            min-height: 100px;
            resize: vertical;
        }

        .error-message {
            padding: 13px 15px;
            margin-bottom: 22px;
            background: #fef2f2;
            border: 1px solid #fecaca;
            color: #b91c1c;
            border-radius: 8px;
            font-size: 14px;
        }

        .form-actions {
            display: flex;
            justify-content: flex-end;
            gap: 12px;
            margin-top: 28px;
            padding-top: 22px;
            border-top: 1px solid #e2e8f0;
        }

        .btn {
            display: inline-block;
            padding: 12px 18px;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            font-size: 14px;
            text-decoration: none;
            cursor: pointer;
        }

        .btn-primary {
            background: #2563eb;
            color: white;
        }

        .btn-primary:hover {
            background: #1d4ed8;
        }

        .btn-secondary {
            background: #e2e8f0;
            color: #334155;
        }

        .btn-secondary:hover {
            background: #cbd5e1;
        }

        @media (max-width: 600px) {
            .form-card {
                padding: 20px;
            }

            .form-grid {
                grid-template-columns: 1fr;
            }

            .form-group.full-width {
                grid-column: auto;
            }

            .form-actions {
                flex-direction: column-reverse;
            }

            .form-actions .btn {
                width: 100%;
                text-align: center;
            }
        }
    </style>
</head>

<body>

<div class="dashboard">

    <aside class="sidebar">

        <div class="logo">
            MEDICAL <span>ASSIST</span>
        </div>

        <ul class="nav">
            <li>
                <a href="<%= context %>/dashboard.jsp">Dashboard</a>
            </li>

            <li>
                <a href="<%= context %>/patients" class="active">Patients</a>
            </li>

            <li><a href="#">Appointments</a></li>
            <li><a href="#">Diagnosis Assistance</a></li>
            <li><a href="#">Reports</a></li>

            <li>
                <a href="<%= context %>/logout">Logout</a>
            </li>
        </ul>

    </aside>

    <main class="main-content">

        <div class="edit-container">

            <div class="edit-header">
                <div>
                    <h1>Edit Patient</h1>
                    <p>Update the registered patient's information.</p>
                </div>
            </div>

            <div class="form-card">

                <div class="patient-id">
                    Patient ID: <%= patient.getId() %>
                </div>

                <% if (request.getAttribute("error") != null) { %>
                    <div class="error-message" role="alert">
                        <%= escapeHtml(
                            String.valueOf(request.getAttribute("error"))
                        ) %>
                    </div>
                <% } %>

                <form action="<%= context %>/patients"
                      method="post">

                    <input type="hidden" name="action" value="update">

                    <input type="hidden"
                           name="id"
                           value="<%= patient.getId() %>">

                    <div class="form-grid">

                        <div class="form-group">
                            <label for="name">Full Name *</label>

                            <input type="text"
                                   id="name"
                                   name="name"
                                   value="<%= escapeHtml(patient.getName()) %>"
                                   maxlength="100"
                                   required>
                        </div>

                        <div class="form-group">
                            <label for="age">Age *</label>

                            <input type="number"
                                   id="age"
                                   name="age"
                                   value="<%= patient.getAge() %>"
                                   min="1"
                                   max="120"
                                   required>
                        </div>

                        <div class="form-group">
                            <label for="gender">Gender</label>

                            <select id="gender" name="gender">
                                <option value=""
                                    <%= patient.getGender() == null
                                        || patient.getGender().isBlank()
                                        ? "selected" : "" %>>
                                    Select gender
                                </option>

                                <option value="Male"
                                    <%= "Male".equalsIgnoreCase(patient.getGender())
                                        ? "selected" : "" %>>
                                    Male
                                </option>

                                <option value="Female"
                                    <%= "Female".equalsIgnoreCase(patient.getGender())
                                        ? "selected" : "" %>>
                                    Female
                                </option>

                                <option value="Other"
                                    <%= "Other".equalsIgnoreCase(patient.getGender())
                                        ? "selected" : "" %>>
                                    Other
                                </option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="bloodGroup">Blood Group</label>

                            <select id="bloodGroup" name="bloodGroup">
                                <option value=""
                                    <%= patient.getBloodGroup() == null
                                        || patient.getBloodGroup().isBlank()
                                        ? "selected" : "" %>>
                                    Select blood group
                                </option>

                                <%
                                    String[] bloodGroups = {
                                        "A+", "A-", "B+", "B-",
                                        "AB+", "AB-", "O+", "O-"
                                    };

                                    for (String group : bloodGroups) {
                                %>
                                    <option value="<%= group %>"
                                        <%= group.equalsIgnoreCase(
                                                patient.getBloodGroup() == null
                                                    ? ""
                                                    : patient.getBloodGroup())
                                            ? "selected" : "" %>>
                                        <%= group %>
                                    </option>
                                <%
                                    }
                                %>
                            </select>
                        </div>

                        <div class="form-group">
                            <label for="phone">Phone</label>

                            <input type="tel"
                                   id="phone"
                                   name="phone"
                                   value="<%= escapeHtml(patient.getPhone()) %>"
                                   maxlength="20">
                        </div>

                        <div class="form-group">
                            <label for="email">Email</label>

                            <input type="email"
                                   id="email"
                                   name="email"
                                   value="<%= escapeHtml(patient.getEmail()) %>"
                                   maxlength="150">
                        </div>

                        <div class="form-group full-width">
                            <label for="address">Address</label>

                            <textarea id="address"
                                      name="address"
                                      maxlength="500"><%= escapeHtml(patient.getAddress()) %></textarea>
                        </div>

                    </div>

                    <div class="form-actions">

                        <a href="<%= context %>/patients"
                           class="btn btn-secondary">
                            Cancel
                        </a>

                        <button type="submit"
                                class="btn btn-primary">
                            Save Changes
                        </button>

                    </div>

                </form>

            </div>

        </div>

    </main>

</div>

</body>
</html>
