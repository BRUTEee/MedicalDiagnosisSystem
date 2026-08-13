<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>Doctor Login - Medical Assist</title>

    <link rel="stylesheet" href="css/login.css">

</head>

<body>

<div class="login-container">

    <div class="login-card">

        <div class="logo">
            MEDICAL <span>ASSIST</span>
        </div>

        <h1>Doctor Login</h1>

        <p class="subtitle">
            Sign in to access the medical assistance system.
        </p>

        <form action="login" method="post">

            <div class="form-group">

                <label>Email</label>

                <input
                    type="email"
                    name="email"
                    placeholder="Enter your email"
                    required
                >

            </div>


            <div class="form-group">

                <label>Password</label>

                <input
                    type="password"
                    name="password"
                    placeholder="Enter your password"
                    required
                >

            </div>


            <button type="submit" class="login-btn">
                Sign In
            </button>

        </form>


        <p class="error">
            ${error}
        </p>


        <a href="index.jsp" class="back-home">
            &larr; Back to Home
        </a>

    </div>

</div>

</body>

</html>