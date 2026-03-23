<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

<div class="card shadow-sm border-0">
    <div class="card-body">
        <h4 class="mb-3">${patient.id == null ? 'Add Patient' : 'Update Patient'}</h4>
        <form action="${pageContext.request.contextPath}${actionPath}" method="post" id="patientForm" novalidate>
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">

            <div class="row g-3">
                <div class="col-md-6">
                    <label class="form-label">Name</label>
                    <input type="text" name="name" class="form-control" value="${patient.name}" required>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Age</label>
                    <input type="number" min="0" max="140" name="age" class="form-control" value="${patient.age}" required>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Gender</label>
                    <select name="gender" class="form-select" required>
                        <option value="">Select Gender</option>
                        <c:forEach items="${genders}" var="gender">
                            <option value="${gender}" ${patient.gender == gender ? 'selected' : ''}>${gender}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Contact Number</label>
                    <input type="text" name="contactNumber" class="form-control" pattern="[0-9+\\- ]{8,20}"
                           value="${patient.contactNumber}" required>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Email</label>
                    <input type="email" name="email" class="form-control" value="${patient.email}">
                </div>
                <div class="col-md-12">
                    <label class="form-label">Address</label>
                    <input type="text" name="address" class="form-control" value="${patient.address}" required>
                </div>
                <div class="col-md-12">
                    <label class="form-label">Medical History</label>
                    <textarea name="medicalHistory" class="form-control" rows="4">${patient.medicalHistory}</textarea>
                </div>
            </div>

            <div class="mt-4 d-flex gap-2">
                <button type="submit" class="btn btn-primary">Save</button>
                <a href="${pageContext.request.contextPath}/admin/patients" class="btn btn-outline-secondary">Cancel</a>
            </div>
        </form>
    </div>
</div>

<script>
    document.getElementById('patientForm').addEventListener('submit', function (e) {
        if (!this.checkValidity()) {
            e.preventDefault();
            this.classList.add('was-validated');
        }
    });
</script>

<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
