package com.hms.service;

import com.hms.entity.Appointment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@hospital.local}")
    private String fromAddress;

    public void sendAppointmentConfirmation(Appointment appointment) {
        sendMail(
            appointment.getPatient().getEmail(),
            "Appointment Confirmation - HMS",
            "Dear " + appointment.getPatient().getName() + ", your appointment with Dr. " +
                appointment.getDoctor().getName() + " is scheduled on " + appointment.getAppointmentDate() +
                " at " + appointment.getTimeSlot() + "."
        );
    }

    public void sendCancellationNotification(Appointment appointment) {
        sendMail(
            appointment.getPatient().getEmail(),
            "Appointment Cancellation - HMS",
            "Dear " + appointment.getPatient().getName() + ", your appointment with Dr. " +
                appointment.getDoctor().getName() + " on " + appointment.getAppointmentDate() +
                " at " + appointment.getTimeSlot() + " has been cancelled."
        );
    }

    public void sendReminder(Appointment appointment) {
        sendMail(
            appointment.getPatient().getEmail(),
            "Appointment Reminder - HMS",
            "Reminder: You have an appointment with Dr. " + appointment.getDoctor().getName() +
                " on " + appointment.getAppointmentDate() + " at " + appointment.getTimeSlot() + "."
        );
    }

    private void sendMail(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception ex) {
            log.warn("Unable to send email to {} due to {}", to, ex.getMessage());
        }
    }
}
