package com.example.hospital.repository;

import com.example.hospital.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Page<Doctor> findAll(Pageable pageable);
    List<Doctor> findBySpecializationIgnoreCase(String specialization);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndSpecializationIgnoreCase(String name, String specialization);
}
