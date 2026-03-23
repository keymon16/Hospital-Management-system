package com.hms.controller;

import com.hms.service.AppointmentService;
import com.hms.service.AuditLogService;
import com.hms.service.DashboardService;
import com.hms.service.PatientService;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final DashboardService dashboardService;
    private final AppointmentService appointmentService;
    private final PatientService patientService;
    private final AuditLogService auditLogService;

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        int year = Year.now().getValue();
        Map<Integer, Long> patientStats = patientService.monthlyPatientRegistrations(year);
        Map<Integer, Long> appointmentStats = appointmentService.monthlyAnalytics(year);

        model.addAttribute("stats", dashboardService.adminStats());
        model.addAttribute("recentAppointments", appointmentService.getRecentAppointments());
        model.addAttribute("recentLogs", auditLogService.getRecentLogs());
        model.addAttribute("monthLabels", monthLabels());
        model.addAttribute("patientData", monthlySeries(patientStats));
        model.addAttribute("appointmentData", monthlySeries(appointmentStats));
        return "admin/dashboard";
    }

    @GetMapping("/admin/reports")
    public String reportsDashboard(Model model) {
        int year = Year.now().getValue();
        model.addAttribute("currentYear", year);
        model.addAttribute("monthLabels", monthLabels());
        model.addAttribute("patientData", monthlySeries(patientService.monthlyPatientRegistrations(year)));
        model.addAttribute("appointmentData", monthlySeries(appointmentService.monthlyAnalytics(year)));
        model.addAttribute("recentLogs", auditLogService.getRecentLogs());
        return "admin/reports";
    }

    private List<String> monthLabels() {
        return List.of(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        );
    }

    private List<Long> monthlySeries(Map<Integer, Long> source) {
        List<Long> data = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            data.add(source.getOrDefault(month, 0L));
        }
        return data;
    }
}
