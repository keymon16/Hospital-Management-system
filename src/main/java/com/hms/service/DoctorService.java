package com.hms.service;

import com.hms.entity.Doctor;
import com.hms.enums.DoctorStatus;
import com.hms.repository.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final FileStorageService fileStorageService;
    private final AuditLogService auditLogService;

    @Transactional(readOnly = true)
    public Page<Doctor> getDoctors(String specializationFilter, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        if (specializationFilter != null && !specializationFilter.isBlank()) {
            return doctorRepository.findBySpecializationContainingIgnoreCase(specializationFilter.trim(), pageable);
        }
        return doctorRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Doctor not found with ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Doctor> getActiveDoctors() {
        return doctorRepository.findByStatus(DoctorStatus.ACTIVE);
    }

    @Transactional
    public Doctor createDoctor(Doctor doctor, MultipartFile profilePhoto, String actor) {
        if (doctorRepository.existsByEmailIgnoreCase(doctor.getEmail())) {
            throw new IllegalArgumentException("Doctor email already exists");
        }
        String photoPath = fileStorageService.saveDoctorPhoto(profilePhoto);
        if (photoPath != null) {
            doctor.setProfilePhotoPath(photoPath);
        }
        Doctor saved = doctorRepository.save(doctor);
        auditLogService.log(actor, "DOCTOR_CREATE", "Doctor created: ID=" + saved.getId() + ", Name=" + saved.getName());
        return saved;
    }

    @Transactional
    public Doctor updateDoctor(Long id, Doctor updatedDoctor, MultipartFile profilePhoto, String actor) {
        Doctor existing = getDoctorById(id);
        if (doctorRepository.existsByEmailIgnoreCaseAndIdNot(updatedDoctor.getEmail(), id)) {
            throw new IllegalArgumentException("Another doctor already uses this email");
        }
        existing.setName(updatedDoctor.getName());
        existing.setSpecialization(updatedDoctor.getSpecialization());
        existing.setContactNumber(updatedDoctor.getContactNumber());
        existing.setEmail(updatedDoctor.getEmail());
        existing.setAvailabilitySchedule(updatedDoctor.getAvailabilitySchedule());
        existing.setConsultationFee(updatedDoctor.getConsultationFee());
        existing.setStatus(updatedDoctor.getStatus());

        String photoPath = fileStorageService.saveDoctorPhoto(profilePhoto);
        if (photoPath != null) {
            existing.setProfilePhotoPath(photoPath);
        }

        Doctor saved = doctorRepository.save(existing);
        auditLogService.log(actor, "DOCTOR_UPDATE", "Doctor updated: ID=" + saved.getId());
        return saved;
    }

    @Transactional
    public void deleteDoctor(Long id, String actor) {
        Doctor existing = getDoctorById(id);
        doctorRepository.delete(existing);
        auditLogService.log(actor, "DOCTOR_DELETE", "Doctor deleted: ID=" + id + ", Name=" + existing.getName());
    }
}
