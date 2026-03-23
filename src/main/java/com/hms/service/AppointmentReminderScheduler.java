package com.hms.service;

import com.hms.entity.Appointment;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentReminderScheduler {

    private final AppointmentService appointmentService;
    private final EmailService emailService;

    // Every day at 08:00 AM server time.
    @Scheduled(cron = "0 0 8 * * *")
    public void sendUpcomingAppointmentReminders() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Appointment> appointments = appointmentService.upcomingAppointmentsForReminder(tomorrow, tomorrow);
        for (Appointment appointment : appointments) {
            emailService.sendReminder(appointment);
        }
        if (!appointments.isEmpty()) {
            log.info("Sent {} appointment reminders for {}", appointments.size(), tomorrow);
        }
    }
}
