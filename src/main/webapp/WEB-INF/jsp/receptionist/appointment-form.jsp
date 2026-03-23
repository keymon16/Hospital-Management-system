<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

<div class="card shadow-sm border-0">
    <div class="card-body">
        <h4 class="mb-3">${appointmentForm.id == null ? 'Book Appointment' : 'Update Appointment'}</h4>

        <form action="${pageContext.request.contextPath}${actionPath}" method="post" id="appointmentForm" novalidate>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label">Patient</label>
                    <select name="patientId" class="form-select" required>
                        <option value="">Select Patient</option>
                        <c:forEach items="${patients}" var="patient">
                            <option value="${patient.id}" ${appointmentForm.patientId == patient.id ? 'selected' : ''}>
                                    ${patient.name} (${patient.contactNumber})
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Doctor</label>
                    <select name="doctorId" class="form-select" required>
                        <option value="">Select Doctor</option>
                        <c:forEach items="${doctors}" var="doctor">
                            <option value="${doctor.id}" ${appointmentForm.doctorId == doctor.id ? 'selected' : ''}>
                                    ${doctor.name} - ${doctor.specialization}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Appointment Date</label>
                    <input type="date" name="appointmentDate" class="form-control"
                           value="${appointmentForm.appointmentDate}" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Time Slot</label>
                    <input type="text" name="timeSlot" class="form-control" placeholder="10:00 AM - 10:30 AM"
                           value="${appointmentForm.timeSlot}" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Status</label>
                    <select name="status" class="form-select">
                        <c:forEach items="${statuses}" var="status">
                            <option value="${status}" ${appointmentForm.status == status ? 'selected' : ''}>${status}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-12">
                    <label class="form-label">Notes</label>
                    <textarea name="notes" class="form-control" rows="3">${appointmentForm.notes}</textarea>
                </div>
            </div>

            <div class="mt-4 d-flex gap-2">
                <button class="btn btn-primary" type="submit">Save</button>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/receptionist/appointments">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script>
    document.getElementById('appointmentForm').addEventListener('submit', function (e) {
        if (!this.checkValidity()) {
            e.preventDefault();
            this.classList.add('was-validated');
        }
    });
</script>

<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
