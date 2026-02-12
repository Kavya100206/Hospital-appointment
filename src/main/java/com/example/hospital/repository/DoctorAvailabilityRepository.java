package com.example.hospital.repository;

import com.example.hospital.model.DayOfWeek;
import com.example.hospital.model.Doctor;
import com.example.hospital.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {
    List<DoctorAvailability> findByDoctorAndIsActiveTrue(Doctor doctor);
    List<DoctorAvailability> findByDoctorAndDayOfWeekAndIsActiveTrue(Doctor doctor, DayOfWeek dayOfWeek);
    boolean existsByDoctorAndDayOfWeek(Doctor doctor, DayOfWeek dayOfWeek);
}
