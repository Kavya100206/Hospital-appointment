package com.example.hospital.controller;

import com.example.hospital.dto.AppointmentRequest;
import com.example.hospital.model.Appointment;
import com.example.hospital.model.AppointmentStatus;
import com.example.hospital.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public Appointment bookAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication
    ) {
        return appointmentService.bookAppointment(
                request,
                authentication.getName()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public Appointment cancelAppointment(
            @PathVariable Long id,
            Authentication authentication
    ) {
        return appointmentService.cancelAppointment(
                id,
                authentication.getName()
        );
    }

    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> getDoctorAppointments(
            @PathVariable Long doctorId
    ) {
        return appointmentService.getAppointmentsForDoctor(doctorId);
    }


    @GetMapping("/all")
    public Page<Appointment> getAllAppointments(Pageable pageable) {
        return appointmentService.getAllAppointments(pageable);
    }

    @GetMapping("/analytics/status-count")
    public long countByStatus(@RequestParam AppointmentStatus status) {
        return appointmentService.countAppointmentsByStatus(status);
    }

    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public Appointment rescheduleAppointment(
            @PathVariable Long id,
            @Valid @RequestBody com.example.hospital.dto.RescheduleAppointmentRequest request,
            Authentication authentication
    ) {
        return appointmentService.rescheduleAppointment(
                id,
                authentication.getName(),
                request.getNewAppointmentDate(),
                request.getNewAppointmentTime()
        );
    }

}
