
<%@ page import="java.util.List"%>
<%@ page import="com.medical.model.Patient"%>
<%@ page import="com.medical.model.User"%>

<%
User user = (User) session.getAttribute("user");

if (user == null) {
	response.sendRedirect(request.getContextPath() + "/login.jsp");
	return;
}

String context = request.getContextPath();

List<Patient> patients = (List<Patient>) request.getAttribute("patients");
%>

<%!private String escapeHtml(String value) {
		if (value == null) {
			return "Not provided";
		}

		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&#x27;");
	}%>

<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Patients | Medical Diagnosis System</title>

<link rel="stylesheet" href="<%=context%>/css/dashboard.css">

<style>
.page-actions {
	display: flex;
	justify-content: space-between;
	align-items: center;
	gap: 16px;
	flex-wrap: wrap;
	margin-bottom: 25px;
}

.page-actions h1 {
	margin-bottom: 8px;
}

.page-actions p {
	color: #64748b;
	margin-top: 0;
}

.table-card {
	background: white;
	border-radius: 12px;
	padding: 25px;
	box-shadow: 0 3px 12px rgba(0, 0, 0, 0.06);
	overflow-x: auto;
}

table {
	width: 100%;
	border-collapse: collapse;
	min-width: 850px;
}

th, td {
	padding: 14px;
	text-align: left;
	border-bottom: 1px solid #e2e8f0;
}

th {
	background: #f8fafc;
	color: #475569;
	font-size: 14px;
	white-space: nowrap;
}

td {
	font-size: 14px;
	color: #334155;
}

tbody tr:hover {
	background: #f8fafc;
}

.add-btn {
	display: inline-block;
	text-decoration: none;
	background: #2563eb;
	color: white;
	padding: 11px 18px;
	border-radius: 7px;
	font-weight: bold;
	white-space: nowrap;
}

.add-btn:hover {
	background: #1d4ed8;
}

.view-btn {
	display: inline-block;
	text-decoration: none;
	background: #e0f2fe;
	color: #0369a1;
	padding: 8px 12px;
	border-radius: 6px;
	font-weight: bold;
	font-size: 13px;
	white-space: nowrap;
}

.view-btn:hover {
	background: #bae6fd;
}

.empty {
	text-align: center;
	padding: 35px;
	color: #64748b;
}

@media ( max-width : 600px) {
	.table-card {
		padding: 12px;
	}
	.page-actions {
		align-items: flex-start;
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

				<li><a href="<%=context%>/dashboard.jsp">Dashboard</a></li>

				<li><a href="<%=context%>/patients" class="active">Patients</a>
				</li>

				<li><a href="#">Appointments</a></li>

				<li><a href="#">Diagnosis Assistance</a></li>

				<li><a href="#">Reports</a></li>

				<li><a href="<%=context%>/logout">Logout</a></li>

			</ul>

		</aside>

		<main class="main-content">

			<div class="page-actions">

				<div>
					<h1>Patients</h1>
					<p>Manage registered patients and view their details.</p>
				</div>

				<a href="<%=context%>/add-patient.jsp" class="add-btn"> + Add
					Patient </a>

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
							<th>Actions</th>
						</tr>
					</thead>

					<tbody>

						<%
						if (patients != null && !patients.isEmpty()) {

							for (Patient patient : patients) {
						%>

						<tr>

							<td><%=patient.getId()%></td>

							<td><strong><%=escapeHtml(patient.getName())%></strong></td>

							<td><%=patient.getAge()%></td>

							<td><%=escapeHtml(patient.getGender())%></td>

							<td><%=escapeHtml(patient.getBloodGroup())%></td>

							<td><%=escapeHtml(patient.getPhone())%></td>

							<td><%=escapeHtml(patient.getEmail())%></td>


                        <td>
                            <a
                                href="<%= context %>/patient-details?id=<%= patient.getId() %>"
                                class="view-btn">
                                View
                            </a>

                            <a
                                href="<%= context %>/patients?action=edit&id=<%= patient.getId() %>"
                                class="view-btn">
                                Edit
                            </a>
                        </td>

						</tr>

						<%
						}

						} else {
						%>

						<tr>
							<td colspan="8" class="empty">No patients registered yet.
								Click "Add Patient" to register the first patient.</td>
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
