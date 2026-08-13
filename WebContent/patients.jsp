<%@ page import="java.util.List" %>
<%@ page import="com.medical.model.Patient" %>
<%@ page import="com.medical.model.User" %>

<%
    User user = (User) session.getAttribute("user");

    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    List<Patient> patients =
        (List<Patient>) request.getAttribute("patients");
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>Patients</title>

    <link rel="stylesheet" href="css/dashboard.css">

    <style>

        .page-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 25px;
        }

        .table-card {
            background: white;
            border-radius: 12px;
            padding: 25px;
            box-shadow: 0 3px 12px rgba(0,0,0,0.06);
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th,
        td {
            padding: 14px;
            text-align: left;
            border-bottom: 1px solid #e2e8f0;
        }

        th {
            background: #f8fafc;
            color: #475569;
            font-size: 14px;
        }

        td {
            font-size: 14px;
        }

        .add-btn {
            text-decoration: none;
            background: #2563eb;
            color: white;
            padding: 11px 18px;
            border-radius: 7px;
            font-weight: bold;
        }

        .add-btn:hover {
            background: #1d4ed8;
        }

        .empty {
            text-align: center;
            padding: 35px;
            color: #64748b;
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
                <a href="dashboard.jsp">Dashboard</a>
            </li>

            <li>
                <a href="patients" class="active">Patients</a>
            </li>

            <li>
                <a href="#">Appointments</a>
            </li>

            <li>
                <a href="#">Diagnosis Assistance</a>
            </li>

            <li>
                <a href="#">Reports</a>
            </li>

            <li>
                <a href="login.jsp">Logout</a>
            </li>

        </ul>

    </aside>


    <main class="main-content">

        <div class="page-actions">

            <div>

                <h1>Patients</h1>

                <p>Manage registered patients.</p>

            </div>

            <a href="add-patient.jsp" class="add-btn">
                + Add Patient
            </a>

        </div>


        <div class="table-card">

            <table>

                <thead>

                    <tr>

                        <th>ID</th>
                        <th>Name</th>
                        <th>Age</th>
                        <th>Gender</th>
                        <th>Blood Group</th>
                        <th>Phone</th>
                        <th>Email</th>

                    </tr>

                </thead>


                <tbody>

                <%
                    if (patients != null && !patients.isEmpty()) {

                        for (Patient patient : patients) {
                %>

                    <tr>

                        <td>
                            <%= patient.getId() %>
                        </td>

                        <td>
                            <strong>
                                <%= patient.getName() %>
                            </strong>
                        </td>

                        <td>
                            <%= patient.getAge() %>
                        </td>

                        <td>
                            <%= patient.getGender() %>
                        </td>

                        <td>
                            <%= patient.getBloodGroup() %>
                        </td>

                        <td>
                            <%= patient.getPhone() %>
                        </td>

                        <td>
                            <%= patient.getEmail() %>
                        </td>

                    </tr>

                <%
                        }

                    } else {
                %>

                    <tr>

                        <td colspan="7" class="empty">

                            No patients registered yet.

                        </td>

                    </tr>

                <%
                    }
                %>

                </tbody>

            </table>

        </div>

    </main>

</div>

</body>

</html>