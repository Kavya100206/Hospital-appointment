package com.example.hospital.service;

import com.example.hospital.model.Doctor;
import com.example.hospital.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import org.springframework.cache.annotation.Cacheable;


@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;

    @CacheEvict(value = "doctors", allEntries = true)
    public Doctor addDoctor(Doctor doctor) {
        if (doctorRepository.existsByNameIgnoreCaseAndSpecializationIgnoreCase(
                doctor.getName(), doctor.getSpecialization())) {
            throw new com.example.hospital.exception.DuplicateDoctorException(
                "Doctor with name '" + doctor.getName() + 
                "' and specialization '" + doctor.getSpecialization() + "' already exists"
            );
        }
        return doctorRepository.save(doctor);
    }

    @Cacheable("doctors")
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Page<Doctor> getDoctors(Pageable pageable) {
        return doctorRepository.findAll(pageable);
    }

    public List<Doctor> getDoctorsBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationIgnoreCase(specialization);
    }
}
