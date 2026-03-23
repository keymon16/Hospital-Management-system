<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

<div class="card shadow-sm border-0 mx-auto" style="max-width: 680px;">
    <div class="card-body p-4">
        <h4 class="text-center mb-4">Appointment Slip</h4>
        <table class="table table-bordered">
            <tr>
                <th>Appointment ID</th>
                <td>${appointment.id}</td>
            </tr>
            <tr>
                <th>Patient Name</th>
                <td>${appointment.patient.name}</td>
            </tr>
            <tr>
                <th>Doctor Name</th>
                <td>${appointment.doctor.name}</td>
            </tr>
            <tr>
                <th>Specialization</th>
                <td>${appointment.doctor.specialization}</td>
            </tr>
            <tr>
                <th>Date</th>
                <td>${appointment.appointmentDate}</td>
            </tr>
            <tr>
                <th>Time Slot</th>
                <td>${appointment.timeSlot}</td>
            </tr>
            <tr>
                <th>Status</th>
                <td>${appointment.status}</td>
            </tr>
            <tr>
                <th>Consultation Fee</th>
                <td>${appointment.doctor.consultationFee}</td>
            </tr>
        </table>

        <div class="d-flex justify-content-between mt-3">
            <a href="${pageContext.request.contextPath}/receptionist/appointments" class="btn btn-outline-secondary">
                Back
            </a>
            <a href="${pageContext.request.contextPath}/receptionist/appointments/${appointment.id}/slip/pdf"
               class="btn btn-primary">
                Download PDF
            </a>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
