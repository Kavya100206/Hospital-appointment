package com.example.hospital.controller;

import com.example.hospital.dto.AvailableSlotResponse;
import com.example.hospital.dto.DoctorAvailabilityRequest;
import com.example.hospital.dto.DoctorLeaveRequest;
import com.example.hospital.model.DoctorAvailability;
import com.example.hospital.model.DoctorLeave;
import com.example.hospital.service.DoctorAvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorAvailabilityController {

    private final DoctorAvailabilityService availabilityService;

    @PostMapping("/{doctorId}/availability")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DOCTOR')")
    public DoctorAvailability setAvailability(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorAvailabilityRequest request
    ) {
        return availabilityService.setAvailability(doctorId, request);
    }

    @GetMapping("/{doctorId}/availability")
    public List<DoctorAvailability> getAvailability(@PathVariable Long doctorId) {
        return availabilityService.getDoctorAvailability(doctorId);
    }

    @PostMapping("/{doctorId}/leaves")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_DOCTOR')")
    public DoctorLeave addLeave(
            @PathVariable Long doctorId,
            @Valid @RequestBody DoctorLeaveRequest request
    ) {
        return availabilityService.addLeave(doctorId, request);
    }

    @GetMapping("/{doctorId}/leaves")
    public List<DoctorLeave> getLeaves(@PathVariable Long doctorId) {
        return availabilityService.getDoctorLeaves(doctorId);
    }

    @GetMapping("/{doctorId}/available-slots")
    public List<AvailableSlotResponse> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return availabilityService.getAvailableSlots(doctorId, date);
    }
}
