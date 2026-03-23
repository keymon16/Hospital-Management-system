<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="/WEB-INF/jsp/fragments/header.jspf" %>

<div class="card shadow-sm border-0">
    <div class="card-body">
        <div class="d-flex flex-column flex-md-row gap-2 justify-content-between mb-3">
            <h4 class="mb-0">Appointment Management</h4>
            <a class="btn btn-primary" href="${pageContext.request.contextPath}/receptionist/appointments/new">Book Appointment</a>
        </div>

        <form class="row g-2 mb-3" method="get" action="${pageContext.request.contextPath}/receptionist/appointments">
            <div class="col-md-3">
                <input type="date" name="date" class="form-control" value="${date}">
            </div>
            <div class="col-md-3">
                <select class="form-select" name="status">
                    <option value="">All Status</option>
                    <c:forEach items="${statuses}" var="statusOption">
                        <option value="${statusOption}" ${status == statusOption ? 'selected' : ''}>${statusOption}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="col-md-2">
                <button class="btn btn-outline-primary w-100" type="submit">Filter</button>
            </div>
            <div class="col-md-2">
                <a class="btn btn-outline-secondary w-100"
                   href="${pageContext.request.contextPath}/receptionist/appointments">Reset</a>
            </div>
        </form>

        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Patient</th>
                    <th>Doctor</th>
                    <th>Date</th>
                    <th>Time Slot</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${appointmentPage.content}" var="appointment">
                    <tr>
                        <td>${appointment.id}</td>
                        <td>${appointment.patient.name}</td>
                        <td>${appointment.doctor.name}</td>
                        <td>${appointment.appointmentDate}</td>
                        <td>${appointment.timeSlot}</td>
                        <td>${appointment.status}</td>
                        <td class="d-flex gap-1 flex-wrap">
                            <a class="btn btn-sm btn-outline-primary"
                               href="${pageContext.request.contextPath}/receptionist/appointments/edit/${appointment.id}">Edit</a>
                            <a class="btn btn-sm btn-outline-info"
                               href="${pageContext.request.contextPath}/receptionist/appointments/${appointment.id}/slip">Slip</a>
                            <form action="${pageContext.request.contextPath}/receptionist/appointments/cancel/${appointment.id}"
                                  method="post">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                                <button type="submit" class="btn btn-sm btn-outline-danger"
                                        onclick="return confirm('Cancel appointment #${appointment.id}?')">Cancel</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty appointmentPage.content}">
                    <tr>
                        <td colspan="7" class="text-center text-muted py-3">No appointments available.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <c:if test="${appointmentPage.totalPages > 1}">
            <nav>
                <ul class="pagination">
                    <c:forEach begin="0" end="${appointmentPage.totalPages - 1}" var="i">
                        <li class="page-item ${appointmentPage.number == i ? 'active' : ''}">
                            <a class="page-link"
                               href="${pageContext.request.contextPath}/receptionist/appointments?page=${i}&size=${appointmentPage.size}&date=${date}&status=${status}">
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
