<!DOCTYPE html>
<html>

<head>

<meta charset="UTF-8">

<meta name="viewport" content="width=device-width, initial-scale=1.0">

<title>Medical Diagnosis Assistance System</title>

<style>
* {
	margin: 0;
	padding: 0;
	box-sizing: border-box;
}

body {
	font-family: Arial, Helvetica, sans-serif;
	background: #f8fafc;
	color: #172554;
}

/* Navigation */
.navbar {
	height: 70px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 70px;
	background: white;
	border-bottom: 1px solid #e2e8f0;
}

.logo {
	font-size: 21px;
	font-weight: bold;
}

.logo span {
	color: #2563eb;
}

.nav-login {
	text-decoration: none;
	background: #2563eb;
	color: white;
	padding: 11px 22px;
	border-radius: 7px;
	font-weight: bold;
}

.nav-login:hover {
	background: #1d4ed8;
}

/* Hero */
.hero {
	min-height: 520px;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 70px 10%;
	gap: 50px;
}

.hero-content {
	max-width: 650px;
}

.hero-content h1 {
	font-size: 48px;
	line-height: 1.15;
	margin-bottom: 22px;
}

.hero-content h1 span {
	color: #2563eb;
}

.hero-content p {
	font-size: 18px;
	line-height: 1.7;
	color: #64748b;
	margin-bottom: 30px;
}

.hero-btn {
	display: inline-block;
	text-decoration: none;
	background: #2563eb;
	color: white;
	padding: 14px 26px;
	border-radius: 8px;
	font-weight: bold;
	margin-right: 10px;
}

.hero-btn.secondary {
	background: white;
	color: #2563eb;
	border: 1px solid #2563eb;
}

.hero-btn:hover {
	opacity: 0.9;
}

/* Illustration */
.hero-card {
	width: 350px;
	height: 330px;
	background: white;
	border-radius: 20px;
	box-shadow: 0 10px 35px rgba(0, 0, 0, 0.08);
	display: flex;
	align-items: center;
	justify-content: center;
	text-align: center;
	padding: 30px;
}

.medical-icon {
    width: 90px;
    height: 90px;
    margin: 0 auto 20px auto;
    border-radius: 50%;
    background: #eff6ff;
    color: #2563eb;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 52px;
    font-weight: bold;
}
.hero-card h2 {
	margin-bottom: 10px;
}

.hero-card p {
	color: #64748b;
	line-height: 1.5;
}

/* Features */
.features {
	padding: 70px 10%;
	background: white;
	text-align: center;
}

.features h2 {
	font-size: 32px;
	margin-bottom: 12px;
}

.features-subtitle {
	color: #64748b;
	margin-bottom: 40px;
}

.feature-grid {
	display: grid;
	grid-template-columns: repeat(3, 1fr);
	gap: 25px;
}

.feature-card {
	padding: 30px;
	background: #f8fafc;
	border-radius: 12px;
	text-align: left;
}

.feature-card h3 {
	margin-bottom: 12px;
}

.feature-card p {
	color: #64748b;
	line-height: 1.6;
}

/* Footer */
footer {
	text-align: center;
	padding: 25px;
	background: #172554;
	color: #dbeafe;
}

/* Responsive */
@media ( max-width : 850px) {
	.navbar {
		padding: 0 25px;
	}
	.hero {
		flex-direction: column;
		text-align: center;
		padding: 60px 25px;
	}
	.hero-card {
		width: 100%;
		max-width: 350px;
	}
	.feature-grid {
		grid-template-columns: 1fr;
	}
}
</style>

</head>

<body>

	<!-- Navigation -->

	<nav class="navbar">

		<div class="logo">
			MEDICAL <span>ASSIST</span>
		</div>

		<a href="login.jsp" class="nav-login"> Doctor Login </a>

	</nav>


	<!-- Hero Section -->

	<section class="hero">

		<div class="hero-content">

			<h1>
				Smarter Support for <span>Medical Diagnosis</span>
			</h1>

			<p>A healthcare assistance platform designed to help healthcare
				professionals manage patients and support clinical diagnosis through
				structured patient information and medical analysis.</p>

			<a href="login.jsp" class="hero-btn"> Get Started </a> <a
				href="#features" class="hero-btn secondary"> Explore System </a>

		</div>


		<div class="hero-card">

			<div>

				<div class="medical-icon">+</div>

				<h2>Medical Assistance</h2>

				<p>Centralized patient information, clinical assessment and
					decision-support capabilities.</p>

			</div>

		</div>

	</section>


	<!-- Features -->

	<section class="features" id="features">

		<h2>System Capabilities</h2>

		<p class="features-subtitle">Built to support healthcare
			professionals throughout the clinical workflow.</p>


		<div class="feature-grid">

			<div class="feature-card">

				<h3>Patient Management</h3>

				<p>Register and manage patient information in a centralized
					database.</p>

			</div>


			<div class="feature-card">

				<h3>Clinical Assessment</h3>

				<p>Organize symptoms, medical history and diagnostic information
					for assessment.</p>

			</div>


			<div class="feature-card">

				<h3>Decision Support</h3>

				<p>Provide structured assistance for diagnosis and treatment
					decisions.</p>

			</div>

		</div>

	</section>


	<!-- Footer -->

	<footer> Medical Diagnosis Assistance System </footer>

</body>

</html>