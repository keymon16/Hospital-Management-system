package com.hms.repository;

import com.hms.entity.Appointment;
import com.hms.enums.AppointmentStatus;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Page<Appointment> findByAppointmentDate(LocalDate appointmentDate, Pageable pageable);

    Page<Appointment> findByAppointmentDateAndStatus(LocalDate appointmentDate, AppointmentStatus status, Pageable pageable);

    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

    boolean existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNot(
        Long doctorId,
        LocalDate appointmentDate,
        String timeSlot,
        AppointmentStatus status
    );

    boolean existsByDoctorIdAndAppointmentDateAndTimeSlotAndStatusNotAndIdNot(
        Long doctorId,
        LocalDate appointmentDate,
        String timeSlot,
        AppointmentStatus status,
        Long id
    );

    long countByAppointmentDate(LocalDate appointmentDate);

    List<Appointment> findTop10ByOrderByCreatedAtDesc();

    List<Appointment> findByAppointmentDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, AppointmentStatus status);

    @Query(
        "select month(a.appointmentDate), count(a) from Appointment a " +
        "where year(a.appointmentDate) = :year group by month(a.appointmentDate) order by month(a.appointmentDate)"
    )
    List<Object[]> monthlyAppointmentCounts(@Param("year") int year);
}
