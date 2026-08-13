<%@ page import="com.medical.model.User" %>

<%
    User user = (User) session.getAttribute("user");

    // Prevent unauthorized access
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // Patient count loaded by DashboardServlet
    Object patientCountObj = request.getAttribute("patientCount");
    int patientCount = patientCountObj != null
            ? (Integer) patientCountObj
            : 0;
%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>
        Dashboard - Medical Diagnosis Assistance System
    </title>

    <link rel="stylesheet" href="css/dashboard.css">

</head>

<body>

<div class="dashboard">

    <!-- ==================== SIDEBAR ==================== -->

    <aside class="sidebar">

        <div class="logo">
            MEDICAL <span>ASSIST</span>
        </div>

        <ul class="nav">

            <li>
                <a href="dashboard" class="active">
                    Dashboard
                </a>
            </li>

            <li>
                <a href="patients">
                    Patients
                </a>
            </li>

            <li>
                <a href="#">
                    Appointments
                </a>
            </li>

            <li>
                <a href="#">
                    Diagnosis Assistance
                </a>
            </li>

            <li>
                <a href="#">
                    Reports
                </a>
            </li>

            <li>
                <a href="#">
                    Settings
                </a>
            </li>

            <li>
                <a href="logout">
                    Logout
                </a>
            </li>

        </ul>

    </aside>


    <!-- ==================== MAIN CONTENT ==================== -->

    <main class="main-content">


        <!-- TOP BAR -->

        <div class="topbar">

            <div>

                <h1>Doctor Dashboard</h1>

                <p>
                    Welcome back,
                    <strong><%= user.getName() %></strong>
                </p>

            </div>


            <div class="doctor-info">

                <strong>
                    <%= user.getName() %>
                </strong>

                &nbsp; | &nbsp;

                <%= user.getRole() %>

            </div>

        </div>


        <!-- ==================== STATISTICS ==================== -->

        <div class="stats">


            <!-- TOTAL PATIENTS -->

            <div class="stat-card">

                <h3>
                    Total Patients
                </h3>

                <div class="number">
                    <%= patientCount %>
                </div>

            </div>


            <!-- APPOINTMENTS -->

            <div class="stat-card">

                <h3>
                    Appointments
                </h3>

                <div class="number">
                    0
                </div>

            </div>


            <!-- PENDING ASSESSMENTS -->

            <div class="stat-card">

                <h3>
                    Pending Assessments
                </h3>

                <div class="number">
                    0
                </div>

            </div>

        </div>


        <!-- ==================== QUICK ACTIONS ==================== -->

        <div class="card">

            <h2>
                Quick Actions
            </h2>

            <div class="quick-actions">

                <a href="patients"
                   class="action-btn">

                    Manage Patients

                </a>


                <a href="#"
                   class="action-btn">

                    New Assessment

                </a>


                <a href="#"
                   class="action-btn">

                    View Appointments

                </a>

            </div>

        </div>


        <!-- ==================== SYSTEM STATUS ==================== -->

        <div class="card">

            <h2>
                System Status
            </h2>

            <p>
                Patient management and authentication
                modules are currently active.
            </p>

        </div>


        <!-- ==================== CURRENT USER ==================== -->

        <div class="card">

            <h2>
                Account Information
            </h2>

            <p>
                <strong>Name:</strong>
                <%= user.getName() %>
            </p>

            <p>
                <strong>Email:</strong>
                <%= user.getEmail() %>
            </p>

            <p>
                <strong>Role:</strong>
                <%= user.getRole() %>
            </p>

        </div>


    </main>

</div>

</body>

</html>