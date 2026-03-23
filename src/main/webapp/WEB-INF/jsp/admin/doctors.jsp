<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

<div class="card shadow-sm border-0">
    <div class="card-body">
        <div class="d-flex flex-column flex-md-row gap-2 justify-content-between mb-3">
            <h4 class="mb-0">Doctor Management</h4>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/admin/doctors/new">Add Doctor</a>
        </div>

        <form class="row g-2 mb-3" method="get" action="${pageContext.request.contextPath}/admin/doctors">
            <div class="col-md-4">
                <input type="text" name="specialization" class="form-control" placeholder="Search by specialization"
                       value="${specialization}">
            </div>
            <div class="col-md-2">
                <button class="btn btn-outline-primary w-100" type="submit">Search</button>
            </div>
            <div class="col-md-2">
                <a class="btn btn-outline-secondary w-100" href="${pageContext.request.contextPath}/admin/doctors">Reset</a>
            </div>
        </form>

        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Photo</th>
                    <th>Name</th>
                    <th>Specialization</th>
                    <th>Contact</th>
                    <th>Email</th>
                    <th>Schedule</th>
                    <th>Fee</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${doctorPage.content}" var="doctor">
                    <tr>
                        <td>${doctor.id}</td>
                        <td>
                            <c:if test="${not empty doctor.profilePhotoPath}">
                                <img src="${pageContext.request.contextPath}${doctor.profilePhotoPath}"
                                     alt="Doctor Photo" width="48" height="48" class="rounded-circle object-fit-cover">
                            </c:if>
                        </td>
                        <td>${doctor.name}</td>
                        <td>${doctor.specialization}</td>
                        <td>${doctor.contactNumber}</td>
                        <td>${doctor.email}</td>
                        <td>${doctor.availabilitySchedule}</td>
                        <td><fmt:formatNumber value="${doctor.consultationFee}" type="currency"/></td>
                        <td>${doctor.status}</td>
                        <td class="d-flex gap-1">
                            <a class="btn btn-sm btn-outline-primary"
                               href="${pageContext.request.contextPath}/admin/doctors/edit/${doctor.id}">Edit</a>
                            <form action="${pageContext.request.contextPath}/admin/doctors/delete/${doctor.id}" method="post">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                <button class="btn btn-sm btn-outline-danger" type="submit"
                                        onclick="return confirm('Delete doctor #${doctor.id}?')">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty doctorPage.content}">
                    <tr>
                        <td colspan="10" class="text-center text-muted py-3">No doctors found.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <c:if test="${doctorPage.totalPages > 1}">
            <nav>
                <ul class="pagination">
                    <c:forEach begin="0" end="${doctorPage.totalPages - 1}" var="i">
                        <li class="page-item ${doctorPage.number == i ? 'active' : ''}">
                            <a class="page-link"
                               href="${pageContext.request.contextPath}/admin/doctors?page=${i}&size=${doctorPage.size}&specialization=${specialization}">
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
