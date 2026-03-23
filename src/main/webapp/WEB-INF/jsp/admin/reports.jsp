<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h4 class="mb-0">Reports Dashboard (${currentYear})</h4>
    <a class="btn btn-outline-primary" href="${pageContext.request.contextPath}/admin/dashboard">Back to Dashboard</a>
</div>

<div class="row g-3">
    <div class="col-lg-6">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white">Monthly Patient Statistics</div>
            <div class="card-body">
                <canvas id="patientChart" height="140"></canvas>
            </div>
        </div>
    </div>
    <div class="col-lg-6">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white">Appointment Analytics</div>
            <div class="card-body">
                <canvas id="appointmentChart" height="140"></canvas>
            </div>
        </div>
    </div>
</div>

<div class="card shadow-sm border-0 mt-3">
    <div class="card-header bg-white">Audit Logs</div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-sm mb-0">
                <thead>
                <tr>
                    <th>Time</th>
                    <th>User</th>
                    <th>Action</th>
                    <th>Details</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${recentLogs}" var="log">
                    <tr>
                        <td>${log.timestamp}</td>
                        <td>${log.userName}</td>
                        <td>${log.action}</td>
                        <td>${log.details}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    (function () {
        const labels = [
            <c:forEach items="${monthLabels}" var="m" varStatus="s">'${m}'<c:if test="${!s.last}">,</c:if></c:forEach>
        ];
        const patientData = [
            <c:forEach items="${patientData}" var="value" varStatus="s">${value}<c:if test="${!s.last}">,</c:if></c:forEach>
        ];
        const appointmentData = [
            <c:forEach items="${appointmentData}" var="value" varStatus="s">${value}<c:if test="${!s.last}">,</c:if></c:forEach>
        ];

        new Chart(document.getElementById('patientChart'), {
            type: 'line',
            data: {
                labels,
                datasets: [{
                    label: 'Patients',
                    data: patientData,
                    borderColor: 'rgb(25, 135, 84)',
                    backgroundColor: 'rgba(25, 135, 84, 0.2)'
                }]
            }
        });

        new Chart(document.getElementById('appointmentChart'), {
            type: 'bar',
            data: {
                labels,
                datasets: [{
                    label: 'Appointments',
                    data: appointmentData,
                    backgroundColor: 'rgba(13, 110, 253, 0.65)'
                }]
            }
        });
    })();
</script>

<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
