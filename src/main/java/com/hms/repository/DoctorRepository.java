package com.hms.repository;

import com.hms.entity.Doctor;
import com.hms.enums.DoctorStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Page<Doctor> findBySpecializationContainingIgnoreCase(String specialization, Pageable pageable);

    List<Doctor> findByStatus(DoctorStatus status);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
