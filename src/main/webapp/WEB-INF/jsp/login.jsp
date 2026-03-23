<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Hospital Management System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet">
</head>
<body class="login-page">
<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-5 col-lg-4">
            <div class="card shadow border-0">
                <div class="card-body p-4">
                    <h3 class="text-center mb-3">Hospital Login</h3>
                    <p class="text-muted text-center small">Role based authentication</p>
                    <c:if test="${param.error == 'true'}">
                        <div class="alert alert-danger py-2">Invalid username or password.</div>
                    </c:if>
                    <c:if test="${param.logout == 'true'}">
                        <div class="alert alert-success py-2">You have been logged out.</div>
                    </c:if>
                    <c:if test="${param.expired == 'true'}">
                        <div class="alert alert-warning py-2">Session expired. Please login again.</div>
                    </c:if>
                    <form method="post" action="${pageContext.request.contextPath}/login" class="needs-validation" novalidate>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                        <div class="mb-3">
                            <label for="username" class="form-label">Username</label>
                            <input id="username" name="username" type="text" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <input id="password" name="password" type="password" class="form-control" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Login</button>
                    </form>
                    <div class="small text-muted mt-3">
                        Default Admin: <code>admin / admin123</code><br>
                        Default Receptionist: <code>reception / recept123</code>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
