<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

<div class="card shadow-sm border-0">
    <div class="card-body">
        <h4 class="mb-3">${doctor.id == null ? 'Add Doctor' : 'Update Doctor'}</h4>
        <form action="${pageContext.request.contextPath}${actionPath}" method="post" enctype="multipart/form-data"
              id="doctorForm" novalidate>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label">Name</label>
                    <input type="text" name="name" class="form-control" value="${doctor.name}" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Specialization</label>
                    <input type="text" name="specialization" class="form-control" value="${doctor.specialization}" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Contact Number</label>
                    <input type="text" name="contactNumber" class="form-control" pattern="[0-9+\\- ]{8,20}"
                           value="${doctor.contactNumber}" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Email</label>
                    <input type="email" name="email" class="form-control" value="${doctor.email}" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Availability Schedule</label>
                    <input type="text" name="availabilitySchedule" class="form-control"
                           value="${doctor.availabilitySchedule}" placeholder="Mon-Fri, 10:00 AM - 4:00 PM" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Consultation Fee</label>
                    <input type="number" min="0" step="0.01" name="consultationFee" class="form-control"
                           value="${doctor.consultationFee}" required>
                </div>
                <div class="col-md-3">
                    <label class="form-label">Status</label>
                    <select name="status" class="form-select">
                        <c:forEach items="${statuses}" var="status">
                            <option value="${status}" ${doctor.status == status ? 'selected' : ''}>${status}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Profile Photo</label>
                    <input type="file" name="profilePhoto" class="form-control" accept=".jpg,.jpeg,.png">
                </div>
                <c:if test="${not empty doctor.profilePhotoPath}">
                    <div class="col-md-6">
                        <img src="${pageContext.request.contextPath}${doctor.profilePhotoPath}"
                             class="img-thumbnail" style="max-height:120px;" alt="Doctor Photo">
                    </div>
                </c:if>
            </div>

            <div class="mt-4 d-flex gap-2">
                <button type="submit" class="btn btn-primary">Save</button>
                <a href="${pageContext.request.contextPath}/admin/doctors" class="btn btn-outline-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script>
    document.getElementById('doctorForm').addEventListener('submit', function (e) {
        if (!this.checkValidity()) {
            e.preventDefault();
            this.classList.add('was-validated');
        }
    });
</script>

<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
