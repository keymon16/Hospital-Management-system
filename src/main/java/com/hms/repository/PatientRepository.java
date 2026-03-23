package com.hms.repository;

import com.hms.entity.Patient;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    Page<Patient> findByNameContainingIgnoreCaseOrContactNumberContainingIgnoreCase(
        String name,
        String contactNumber,
        Pageable pageable
    );

    boolean existsByNameIgnoreCaseAndContactNumber(String name, String contactNumber);

    boolean existsByNameIgnoreCaseAndContactNumberAndIdNot(String name, String contactNumber, Long id);

    @Query(
        "select month(p.registrationDate), count(p) from Patient p " +
        "where year(p.registrationDate) = :year group by month(p.registrationDate) order by month(p.registrationDate)"
    )
    List<Object[]> monthlyPatientRegistrations(@Param("year") int year);
}
