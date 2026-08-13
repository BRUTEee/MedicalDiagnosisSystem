<%@ page import="com.medical.model.User" %>

<%
    User user = (User) session.getAttribute("user");

    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>Add Patient</title>

    <link rel="stylesheet" href="css/dashboard.css">

    <style>

        .form-card {
            background: white;
            padding: 30px;
            border-radius: 12px;
            max-width: 800px;
            box-shadow: 0 3px 12px rgba(0,0,0,0.06);
        }

        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        .form-group.full {
            grid-column: 1 / -1;
        }

        .form-group label {
            margin-bottom: 7px;
            font-weight: bold;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            padding: 11px;
            border: 1px solid #cbd5e1;
            border-radius: 7px;
            font-size: 14px;
        }

        .form-group textarea {
            resize: vertical;
            min-height: 80px;
        }

        .submit-btn {
            margin-top: 20px;
            padding: 12px 24px;
            border: none;
            border-radius: 7px;
            background: #2563eb;
            color: white;
            font-weight: bold;
            cursor: pointer;
        }

        .submit-btn:hover {
            background: #1d4ed8;
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
                <a href="patients">Patients</a>
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

        <div class="topbar">

            <div>

                <h1>Add Patient</h1>

                <p>Enter the patient's basic information.</p>

            </div>

        </div>


        <div class="form-card">

            <h2>Patient Information</h2>

            <form action="patients" method="post">

                <div class="form-grid">

                    <div class="form-group">

                        <label>Full Name</label>

                        <input
                            type="text"
                            name="name"
                            required
                        >

                    </div>


                    <div class="form-group">

                        <label>Age</label>

                        <input
                            type="number"
                            name="age"
                            min="1"
                            max="120"
                            required
                        >

                    </div>


                    <div class="form-group">

                        <label>Gender</label>

                        <select name="gender" required>

                            <option value="">Select Gender</option>

                            <option value="Male">Male</option>

                            <option value="Female">Female</option>

                            <option value="Other">Other</option>

                        </select>

                    </div>


                    <div class="form-group">

                        <label>Blood Group</label>

                        <select name="bloodGroup">

                            <option value="">Select Blood Group</option>

                            <option value="A+">A+</option>
                            <option value="A-">A-</option>
                            <option value="B+">B+</option>
                            <option value="B-">B-</option>
                            <option value="AB+">AB+</option>
                            <option value="AB-">AB-</option>
                            <option value="O+">O+</option>
                            <option value="O-">O-</option>

                        </select>

                    </div>


                    <div class="form-group">

                        <label>Phone</label>

                        <input
                            type="tel"
                            name="phone"
                            required
                        >

                    </div>


                    <div class="form-group">

                        <label>Email</label>

                        <input
                            type="email"
                            name="email"
                        >

                    </div>


                    <div class="form-group full">

                        <label>Address</label>

                        <textarea name="address"></textarea>

                    </div>

                </div>


                <button type="submit" class="submit-btn">
                    Add Patient
                </button>

            </form>

        </div>

    </main>

</div>

</body>

</html>