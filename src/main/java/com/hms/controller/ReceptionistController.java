package com.hms.controller;

import com.hms.enums.AppointmentStatus;
import com.hms.service.AppointmentService;
import com.hms.service.DoctorService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ReceptionistController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;

    @GetMapping("/receptionist/dashboard")
    public String receptionistDashboard(Model model) {
        Page<?> todayAppointments = appointmentService.getAppointments(LocalDate.now(), AppointmentStatus.SCHEDULED, 0, 10);
        model.addAttribute("todayAppointments", todayAppointments.getContent());
        model.addAttribute("appointmentsTodayCount", appointmentService.countAppointmentsToday());
        model.addAttribute("activeDoctors", doctorService.getActiveDoctors().size());
        return "receptionist/dashboard";
    }
}
