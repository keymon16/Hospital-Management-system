package com.hms.service;

import com.hms.entity.Patient;
import com.hms.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public Page<Patient> getPatients(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (search != null && !search.isBlank()) {
            String q = search.trim();
            if (q.matches("\\d+")) {
                long id = Long.parseLong(q);
                return patientRepository.findById(id)
                    .<Page<Patient>>map(patient -> new PageImpl<>(java.util.List.of(patient), pageable, 1))
                    .orElseGet(() -> new PageImpl<>(java.util.List.of(), pageable, 0));
            }
            return patientRepository.findByNameContainingIgnoreCaseOrContactNumberContainingIgnoreCase(q, q, pageable);
        }
        return patientRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Patient not found with ID: " + id));
    }

    @Transactional
    public Patient createPatient(Patient patient, String actor) {
        if (patientRepository.existsByNameIgnoreCaseAndContactNumber(patient.getName(), patient.getContactNumber())) {
            throw new IllegalArgumentException("Patient already exists with same name and contact number");
        }
        Patient saved = patientRepository.save(patient);
        auditLogService.log(actor, "PATIENT_CREATE", "Patient created: ID=" + saved.getId() + ", Name=" + saved.getName());
        return saved;
    }

    @Transactional
    public Patient updatePatient(Long id, Patient updatedPatient, String actor) {
        Patient existing = getPatientById(id);
        if (patientRepository.existsByNameIgnoreCaseAndContactNumberAndIdNot(
            updatedPatient.getName(),
            updatedPatient.getContactNumber(),
            id
        )) {
            throw new IllegalArgumentException("Another patient already exists with this name and contact number");
        }
        existing.setName(updatedPatient.getName());
        existing.setAge(updatedPatient.getAge());
        existing.setGender(updatedPatient.getGender());
        existing.setAddress(updatedPatient.getAddress());
        existing.setContactNumber(updatedPatient.getContactNumber());
        existing.setEmail(updatedPatient.getEmail());
        existing.setMedicalHistory(updatedPatient.getMedicalHistory());
        Patient saved = patientRepository.save(existing);
        auditLogService.log(actor, "PATIENT_UPDATE", "Patient updated: ID=" + saved.getId());
        return saved;
    }

    @Transactional
    public void deletePatient(Long id, String actor) {
        Patient patient = getPatientById(id);
        patientRepository.delete(patient);
        auditLogService.log(actor, "PATIENT_DELETE", "Patient deleted: ID=" + id + ", Name=" + patient.getName());
    }

    @Transactional(readOnly = true)
    public Map<Integer, Long> monthlyPatientRegistrations(int year) {
        Map<Integer, Long> data = new TreeMap<>();
        for (Object[] row : patientRepository.monthlyPatientRegistrations(year)) {
            data.put((Integer) row[0], (Long) row[1]);
        }
        return data;
    }
}
