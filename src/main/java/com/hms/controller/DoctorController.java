package com.hms.controller;

import com.hms.entity.Doctor;
import com.hms.enums.DoctorStatus;
import com.hms.service.DoctorService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping
    public String listDoctors(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String specialization,
        Model model
    ) {
        Page<Doctor> doctorPage = doctorService.getDoctors(specialization, page, size);
        model.addAttribute("doctorPage", doctorPage);
        model.addAttribute("specialization", specialization);
        return "admin/doctors";
    }

    @GetMapping("/new")
    public String newDoctorForm(Model model) {
        model.addAttribute("doctor", new Doctor());
        model.addAttribute("statuses", DoctorStatus.values());
        model.addAttribute("actionPath", "/admin/doctors/save");
        return "admin/doctor-form";
    }

    @PostMapping("/save")
    public String saveDoctor(
        @Valid @ModelAttribute("doctor") Doctor doctor,
        BindingResult bindingResult,
        @RequestParam(required = false) MultipartFile profilePhoto,
        Principal principal,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", DoctorStatus.values());
            model.addAttribute("actionPath", "/admin/doctors/save");
            return "admin/doctor-form";
        }
        try {
            doctorService.createDoctor(doctor, profilePhoto, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Doctor created successfully.");
            return "redirect:/admin/doctors";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("statuses", DoctorStatus.values());
            model.addAttribute("actionPath", "/admin/doctors/save");
            return "admin/doctor-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editDoctorForm(@PathVariable Long id, Model model) {
        model.addAttribute("doctor", doctorService.getDoctorById(id));
        model.addAttribute("statuses", DoctorStatus.values());
        model.addAttribute("actionPath", "/admin/doctors/update/" + id);
        return "admin/doctor-form";
    }

    @PostMapping("/update/{id}")
    public String updateDoctor(
        @PathVariable Long id,
        @Valid @ModelAttribute("doctor") Doctor doctor,
        BindingResult bindingResult,
        @RequestParam(required = false) MultipartFile profilePhoto,
        Principal principal,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("statuses", DoctorStatus.values());
            model.addAttribute("actionPath", "/admin/doctors/update/" + id);
            return "admin/doctor-form";
        }
        try {
            doctorService.updateDoctor(id, doctor, profilePhoto, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Doctor updated successfully.");
            return "redirect:/admin/doctors";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("statuses", DoctorStatus.values());
            model.addAttribute("actionPath", "/admin/doctors/update/" + id);
            return "admin/doctor-form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteDoctor(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            doctorService.deleteDoctor(id, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Doctor deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute(
                "errorMessage",
                "Doctor cannot be deleted. It may be linked with existing appointments."
            );
        }
        return "redirect:/admin/doctors";
    }
}
