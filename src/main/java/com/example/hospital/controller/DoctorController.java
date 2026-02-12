package com.example.hospital.controller;

import com.example.hospital.model.Doctor;
import com.example.hospital.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    // ADMIN ONLY
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Doctor addDoctor(@Valid @RequestBody Doctor doctor) {
        return doctorService.addDoctor(doctor);
    }

    // ANY AUTHENTICATED USER
    @GetMapping
    public List<Doctor> getDoctors() {
        return doctorService.getAllDoctors();
    }

    @GetMapping("/paginated")
    public Page<Doctor> getDoctorsPaginated(Pageable pageable) {
        return doctorService.getDoctors(pageable);
    }

    @GetMapping("/specialization")
    public List<Doctor> getBySpecialization(@RequestParam String specialization) {
        return doctorService.getDoctorsBySpecialization(specialization);
    }
}
