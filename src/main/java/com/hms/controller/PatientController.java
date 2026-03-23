package com.hms.controller;

import com.hms.entity.Patient;
import com.hms.enums.Gender;
import com.hms.service.PatientService;
import com.hms.service.PdfService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final PdfService pdfService;

    @GetMapping
    public String listPatients(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String search,
        Model model
    ) {
        Page<Patient> patientPage = patientService.getPatients(search, page, size);
        model.addAttribute("patientPage", patientPage);
        model.addAttribute("search", search);
        return "admin/patients";
    }

    @GetMapping("/new")
    public String newPatientForm(Model model) {
        model.addAttribute("patient", new Patient());
        model.addAttribute("genders", Gender.values());
        model.addAttribute("actionPath", "/admin/patients/save");
        return "admin/patient-form";
    }

    @PostMapping("/save")
    public String savePatient(
        @Valid @ModelAttribute("patient") Patient patient,
        BindingResult bindingResult,
        Principal principal,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", Gender.values());
            model.addAttribute("actionPath", "/admin/patients/save");
            return "admin/patient-form";
        }
        try {
            patientService.createPatient(patient, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Patient added successfully.");
            return "redirect:/admin/patients";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("actionPath", "/admin/patients/save");
            return "admin/patient-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editPatientForm(@PathVariable Long id, Model model) {
        model.addAttribute("patient", patientService.getPatientById(id));
        model.addAttribute("genders", Gender.values());
        model.addAttribute("actionPath", "/admin/patients/update/" + id);
        return "admin/patient-form";
    }

    @PostMapping("/update/{id}")
    public String updatePatient(
        @PathVariable Long id,
        @Valid @ModelAttribute("patient") Patient patient,
        BindingResult bindingResult,
        Principal principal,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("genders", Gender.values());
            model.addAttribute("actionPath", "/admin/patients/update/" + id);
            return "admin/patient-form";
        }
        try {
            patientService.updatePatient(id, patient, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Patient updated successfully.");
            return "redirect:/admin/patients";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("genders", Gender.values());
            model.addAttribute("actionPath", "/admin/patients/update/" + id);
            return "admin/patient-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deletePatient(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            patientService.deletePatient(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Patient deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Patient cannot be deleted. It may be linked with existing appointments."
            );
        }
        return "redirect:/admin/patients";
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<byte[]> downloadPatientReport(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        byte[] pdf = pdfService.generatePatientReport(patient);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=patient-report-" + id + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
