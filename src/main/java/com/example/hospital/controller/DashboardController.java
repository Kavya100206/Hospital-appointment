package com.example.hospital.controller;

import com.example.hospital.dto.AdminDashboardResponse;
import com.example.hospital.dto.DoctorDashboardResponse;
import com.example.hospital.dto.PatientDashboardResponse;
import com.example.hospital.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/patient")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public PatientDashboardResponse getPatientDashboard(Authentication authentication) {
        return dashboardService.getPatientDashboard(authentication.getName());
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public DoctorDashboardResponse getDoctorDashboard(Authentication authentication) {
        return dashboardService.getDoctorDashboard(authentication.getName());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public AdminDashboardResponse getAdminDashboard() {
        return dashboardService.getAdminDashboard();
    }
}
