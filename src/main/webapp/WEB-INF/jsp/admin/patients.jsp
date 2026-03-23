<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

<div class="card shadow-sm border-0">
    <div class="card-body">
        <div class="d-flex flex-column flex-md-row gap-2 justify-content-between mb-3">
            <h4 class="mb-0">Patient Management</h4>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/patients/new">Add Patient</a>
        </div>

        <form class="row g-2 mb-3" method="get" action="${pageContext.request.contextPath}/admin/patients">
            <div class="col-md-4">
                <input type="text" name="search" class="form-control" placeholder="Search by name or phone"
                       value="${search}">
            </div>
            <div class="col-md-2">
                <button class="btn btn-outline-primary w-100" type="submit">Search</button>
            </div>
            <div class="col-md-2">
                <a class="btn btn-outline-secondary w-100" href="${pageContext.request.contextPath}/admin/patients">Reset</a>
            </div>
        </form>

        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Age</th>
                    <th>Gender</th>
                    <th>Contact</th>
                    <th>Email</th>
                    <th>Medical History</th>
                    <th>Registered</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${patientPage.content}" var="patient">
                    <tr>
                        <td>${patient.id}</td>
                        <td>${patient.name}</td>
                        <td>${patient.age}</td>
                        <td>${patient.gender}</td>
                        <td>${patient.contactNumber}</td>
                        <td>${patient.email}</td>
                        <td class="text-truncate" style="max-width: 220px;">${patient.medicalHistory}</td>
                        <td>${patient.registrationDate}</td>
                        <td class="d-flex gap-1">
                            <a class="btn btn-sm btn-outline-primary"
                               href="${pageContext.request.contextPath}/admin/patients/edit/${patient.id}">Edit</a>
                            <a class="btn btn-sm btn-outline-info"
                               href="${pageContext.request.contextPath}/admin/patients/${patient.id}/report">Report</a>
                            <form action="${pageContext.request.contextPath}/admin/patients/delete/${patient.id}" method="post">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                <button class="btn btn-sm btn-outline-danger" type="submit"
                                        onclick="return confirm('Delete patient #${patient.id}?')">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty patientPage.content}">
                    <tr>
                        <td colspan="9" class="text-center text-muted py-3">No patients found.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <c:if test="${patientPage.totalPages > 1}">
            <nav>
                <ul class="pagination">
                    <c:forEach begin="0" end="${patientPage.totalPages - 1}" var="i">
                        <li class="page-item ${patientPage.number == i ? 'active' : ''}">
                            <a class="page-link"
                               href="${pageContext.request.contextPath}/admin/patients?page=${i}&size=${patientPage.size}&search=${search}">
                                    ${i + 1}
                            </a>
                        </li>
                    </c:forEach>
                </ul>
            </nav>
        </c:if>
    </div>
</div>

<%@ include file="/WEB-INF/jsp/fragments/footer.jspf" %>
