package com.hms.service;

import com.hms.dto.AppointmentForm;
import com.hms.entity.Appointment;
import com.hms.entity.Doctor;
import com.hms.entity.Patient;
import com.hms.entity.User;
import com.hms.enums.AppointmentStatus;
import com.hms.repository.AppointmentRepository;
import com.hms.repository.DoctorRepository;
import com.hms.repository.PatientRepository;
import com.hms.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final EmailService emailService;

    @Transactional(readOnly = true)
    public Page<Appointment> getAppointments(LocalDate dateFilter, AppointmentStatus statusFilter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").descending().and(Sort.by("id").descending()));
        if (dateFilter != null && statusFilter != null) {
            return appointmentRepository.findByAppointmentDateAndStatus(dateFilter, statusFilter, pageable);
        }
        if (dateFilter != null) {
            return appointmentRepository.findByAppointmentDate(dateFilter, pageable);
        }
        if (statusFilter != null) {
            return appointmentRepository.findByStatus(statusFilter, pageable);
        }
        return appointmentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Appointment not found with ID: " + id));
    }

    @Transactional
    public Appointment bookAppointment(AppointmentForm form, String actor) {
        validateDoctorSlot(form.getDoctorId(), form.getAppointmentDate(), form.getTimeSlot(), null);
        Appointment appointment = buildAppointmentEntity(form, actor, false);
        Appointment saved = appointmentRepository.save(appointment);
        auditLogService.log(
            actor,
            "APPOINTMENT_BOOK",
            "Appointment booked: ID=" + saved.getId() + ", Date=" + saved.getAppointmentDate() + ", Slot=" + saved.getTimeSlot()
        );
        emailService.sendAppointmentConfirmation(saved);
        return saved;
    }

    @Transactional
    public Appointment updateAppointment(Long id, AppointmentForm form, String actor) {
        Appointment existing = getAppointmentById(id);
        validateDoctorSlot(form.getDoctorId(), form.getAppointmentDate(), form.getTimeSlot(), id);
        Appointment updated = buildAppointmentEntity(form, actor, true);
        existing.setDoctor(updated.getDoctor());
        existing.setPatient(updated.getPatient());
        existing.setAppointmentDate(updated.getAppointmentDate());
        existing.setTimeSlot(updated.getTimeSlot());
        existing.setNotes(updated.getNotes());
        if (form.getStatus() != null) {
            existing.setStatus(form.getStatus());
        }
        Appointment saved = appointmentRepository.save(existing);
        auditLogService.log(actor, "APPOINTMENT_UPDATE", "Appointment updated: ID=" + saved.getId());
        return saved;
    }

    @Transactional
    public void cancelAppointment(Long id, String actor) {
        Appointment existing = getAppointmentById(id);
        existing.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(existing);
        auditLogService.log(actor, "APPOINTMENT_CANCEL", "Appointment cancelled: ID=" + id);
        emailService.sendCancellationNotification(existing);
    }

    @Transactional(readOnly = true)
    public long countAppointmentsToday() {
        return appointmentRepository.countByAppointmentDate(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public Map<Integer, Long> monthlyAnalytics(int year) {
        Map<Integer, Long> data = new TreeMap<>();
        for (Object[] row : appointmentRepository.monthlyAppointmentCounts(year)) {
            data.put((Integer) row[0], (Long) row[1]);
        }
        return data;
    }

    @Transactional(readOnly = true)
    public List<Appointment> getRecentAppointments() {
        return appointmentRepository.findTop10ByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Appointment> upcomingAppointmentsForReminder(LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByAppointmentDateBetweenAndStatus(startDate, endDate, AppointmentStatus.SCHEDULED);
    }

    private Appointment buildAppointmentEntity(AppointmentForm form, String actor, boolean keepStatusIfNull) {
        Patient patient = patientRepository.findById(form.getPatientId())
            .orElseThrow(() -> new EntityNotFoundException("Patient not found with ID: " + form.getPatientId()));
        Doctor doctor = doctorRepository.findById(form.getDoctorId())
            .orElseThrow(() -> new EntityNotFoundException("Doctor not found with ID: " + form.getDoctorId()));
        AppointmentStatus status = form.getStatus();
        if (status == null && !keepStatusIfNull) {
            status = AppointmentStatus.SCHEDULED;
        }
        Appointment appointment = Appointment.builder()
            .patient(patient)
            .doctor(doctor)
            .appointmentDate(form.getAppointmentDate())
            .timeSlot(form.getTimeSlot().trim())
            .notes(form.getNotes())
            .status(status == null ? AppointmentStatus.SCHEDULED : status)
            .build();

        if (actor != null && !actor.isBlank()) {
            User user = userRepository.findByUsername(actor).orElse(null);
            appointment.setCreatedBy(user);
        }
        return appointment;
    }

    private void validateDoctorSlot(Long doctorId, LocalDate date, String timeSlot, Long appointmentId) {
        boolean conflict;
        if (appointmentId == null) {
            conflict = appointmentRepository.existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNot(
                doctorId,
                date,
                timeSlot,
                AppointmentStatus.CANCELLED
            );
        } else {
            conflict = appointmentRepository.existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNotAndIdNot(
                doctorId,
                date,
                timeSlot,
                AppointmentStatus.CANCELLED,
                appointmentId
            );
        }
        if (conflict) {
            throw new IllegalArgumentException("Selected doctor is not available for this date and time slot");
        }
    }
}
