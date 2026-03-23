package com.hms.controller;

import com.hms.dto.AppointmentForm;
import com.hms.entity.Appointment;
import com.hms.enums.AppointmentStatus;
import com.hms.service.AppointmentService;
import com.hms.service.DoctorService;
import com.hms.service.PatientService;
import com.hms.service.PdfService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.time.LocalDate;
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
@RequestMapping("/receptionist/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final PdfService pdfService;

    @GetMapping
    public String listAppointments(
        @RequestParam(required = false) LocalDate date,
        @RequestParam(required = false) AppointmentStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model
    ) {
        Page<Appointment> appointmentPage = appointmentService.getAppointments(date, status, page, size);
        model.addAttribute("appointmentPage", appointmentPage);
        model.addAttribute("date", date);
        model.addAttribute("status", status);
        model.addAttribute("statuses", AppointmentStatus.values());
        return "receptionist/appointments";
    }

    @GetMapping("/new")
    public String newAppointmentForm(Model model) {
        AppointmentForm form = AppointmentForm.builder()
            .appointmentDate(LocalDate.now())
            .status(AppointmentStatus.SCHEDULED)
            .build();
        populateAppointmentFormModel(model, form, "/receptionist/appointments/save");
        return "receptionist/appointment-form";
    }

    @PostMapping("/save")
    public String saveAppointment(
        @Valid @ModelAttribute("appointmentForm") AppointmentForm form,
        BindingResult bindingResult,
        Principal principal,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            populateAppointmentFormModel(model, form, "/receptionist/appointments/save");
            return "receptionist/appointment-form";
        }
        try {
            Appointment appointment = appointmentService.bookAppointment(form, principal.getName());
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Appointment booked successfully. Slip ID: " + appointment.getId()
            );
            return "redirect:/receptionist/appointments";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateAppointmentFormModel(model, form, "/receptionist/appointments/save");
            return "receptionist/appointment-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editAppointmentForm(@PathVariable Long id, Model model) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        AppointmentForm form = AppointmentForm.builder()
            .id(appointment.getId())
            .patientId(appointment.getPatient().getId())
            .doctorId(appointment.getDoctor().getId())
            .appointmentDate(appointment.getAppointmentDate())
            .timeSlot(appointment.getTimeSlot())
            .notes(appointment.getNotes())
            .status(appointment.getStatus())
            .build();
        populateAppointmentFormModel(model, form, "/receptionist/appointments/update/" + id);
        return "receptionist/appointment-form";
    }

    @PostMapping("/update/{id}")
    public String updateAppointment(
        @PathVariable Long id,
        @Valid @ModelAttribute("appointmentForm") AppointmentForm form,
        BindingResult bindingResult,
        Principal principal,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            populateAppointmentFormModel(model, form, "/receptionist/appointments/update/" + id);
            return "receptionist/appointment-form";
        }
        try {
            appointmentService.updateAppointment(id, form, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Appointment updated successfully.");
            return "redirect:/receptionist/appointments";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            populateAppointmentFormModel(model, form, "/receptionist/appointments/update/" + id);
            return "receptionist/appointment-form";
        }
    }

    @PostMapping("/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        appointmentService.cancelAppointment(id, principal.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Appointment cancelled successfully.");
        return "redirect:/receptionist/appointments";
    }

    @GetMapping("/{id}/slip")
    public String viewAppointmentSlip(@PathVariable Long id, Model model) {
        model.addAttribute("appointment", appointmentService.getAppointmentById(id));
        return "receptionist/slip";
    }

    @GetMapping("/{id}/slip/pdf")
    public ResponseEntity<byte[]> downloadSlipPdf(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        byte[] pdf = pdfService.generateAppointmentSlip(appointment);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=appointment-slip-" + id + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }

    private void populateAppointmentFormModel(Model model, AppointmentForm form, String actionPath) {
        model.addAttribute("appointmentForm", form);
        model.addAttribute("patients", patientService.getPatients(null, 0, 200).getContent());
        model.addAttribute("doctors", doctorService.getActiveDoctors());
        model.addAttribute("statuses", AppointmentStatus.values());
        model.addAttribute("actionPath", actionPath);
    }
}
