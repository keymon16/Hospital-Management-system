<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

<div class="row g-3 mb-3">
    <div class="col-md-6">
        <div class="card stat-card bg-primary text-white h-100">
            <div class="card-body">
                <div class="small text-uppercase">Appointments Today</div>
                <div class="display-6 fw-bold">${appointmentsTodayCount}</div>
            </div>
        </div>
    </div>
    <div class="col-md-6">
        <div class="card stat-card bg-success text-white h-100">
            <div class="card-body">
                <div class="small text-uppercase">Active Doctors</div>
                <div class="display-6 fw-bold">${activeDoctors}</div>
            </div>
        </div>
    </div>
</div>

<div class="card shadow-sm border-0">
    <div class="card-header bg-white d-flex justify-content-between align-items-center">
        <span>Today's Scheduled Appointments</span>
        <a class="btn btn-sm btn-primary" href="${pageContext.request.contextPath}/receptionist/appointments/new">Book Appointment</a>
    </div>
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
                <c:forEach items="${todayAppointments}" var="appointment">
                    <tr>
                        <td>${appointment.id}</td>
                        <td>${appointment.patient.name}</td>
                        <td>${appointment.doctor.name}</td>
                        <td>${appointment.appointmentDate}</td>
                        <td>${appointment.timeSlot}</td>
                        <td>${appointment.status}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty todayAppointments}">
                    <tr>
                        <td colspan="6" class="text-center text-muted py-3">No appointments scheduled for today.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
