package com.example.hospital.repository;

import com.example.hospital.model.Doctor;
import com.example.hospital.model.DoctorLeave;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DoctorLeaveRepository extends JpaRepository<DoctorLeave, Long> {
    List<DoctorLeave> findByDoctor(Doctor doctor);
    boolean existsByDoctorAndLeaveDate(Doctor doctor, LocalDate leaveDate);
    List<DoctorLeave> findByDoctorAndLeaveDateBetween(Doctor doctor, LocalDate startDate, LocalDate endDate);
}
