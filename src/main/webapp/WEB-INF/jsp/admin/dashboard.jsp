<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

<div class="row g-3 mb-4">
    <div class="col-md-3">
        <div class="card stat-card bg-primary text-white h-100">
            <div class="card-body">
                <div class="small text-uppercase">Total Doctors</div>
                <div class="display-6 fw-bold">${stats.totalDoctors}</div>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card stat-card bg-success text-white h-100">
            <div class="card-body">
                <div class="small text-uppercase">Total Patients</div>
                <div class="display-6 fw-bold">${stats.totalPatients}</div>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card stat-card bg-warning text-dark h-100">
            <div class="card-body">
                <div class="small text-uppercase">Appointments Today</div>
                <div class="display-6 fw-bold">${stats.appointmentsToday}</div>
            </div>
        </div>
    </div>
    <div class="col-md-3">
        <div class="card stat-card bg-info text-dark h-100">
            <div class="card-body">
                <div class="small text-uppercase">Receptionists</div>
                <div class="display-6 fw-bold">${stats.totalReceptionists}</div>
            </div>
        </div>
    </div>
</div>

<div class="row g-3">
    <div class="col-lg-7">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white fw-semibold">Monthly Analytics</div>
            <div class="card-body">
                <canvas id="adminAnalyticsChart" height="130"></canvas>
            </div>
        </div>
    </div>
    <div class="col-lg-5">
        <div class="card shadow-sm border-0">
            <div class="card-header bg-white fw-semibold">Recent Activity</div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-sm mb-0">
                        <thead>
                        <tr>
                            <th>User</th>
                            <th>Action</th>
                            <th>Time</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${recentLogs}" var="log">
                            <tr>
                                <td>${log.userName}</td>
                                <td>${log.action}</td>
                                <td class="small">${log.timestamp}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="card shadow-sm border-0 mt-3">
    <div class="card-header bg-white fw-semibold">Recent Appointments</div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table mb-0">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Patient</th>
                    <th>Doctor</th>
                    <th>Date</th>
                    <th>Time</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${recentAppointments}" var="appointment">
                    <tr>
                        <td>${appointment.id}</td>
                        <td>${appointment.patient.name}</td>
                        <td>${appointment.doctor.name}</td>
                        <td>${appointment.appointmentDate}</td>
                        <td>${appointment.timeSlot}</td>
                        <td>${appointment.status}</td>
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
        const chartEl = document.getElementById('adminAnalyticsChart');
        if (chartEl) {
            new Chart(chartEl, {
                type: 'bar',
                data: {
                    labels,
                    datasets: [
                        {
                            label: 'Patient Registrations',
                            data: patientData,
                            backgroundColor: 'rgba(25, 135, 84, 0.7)'
                        },
                        {
                            label: 'Appointments',
                            data: appointmentData,
                            backgroundColor: 'rgba(13, 110, 253, 0.7)'
                        }
                    ]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false
                }
            });
        }
    })();
</script>

<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
