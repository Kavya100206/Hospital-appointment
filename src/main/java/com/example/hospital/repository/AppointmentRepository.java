package com.example.hospital.repository;

import com.example.hospital.model.Appointment;
import com.example.hospital.model.AppointmentStatus;
import com.example.hospital.model.Doctor;
import com.example.hospital.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

//    boolean existsByDoctorAndAppointmentDateAndAppointmentTime(
//            Doctor doctor,
//            LocalDate appointmentDate,
//            LocalTime appointmentTime
//    );
    boolean existsByPatientAndDoctorAndAppointmentDateAndStatus(
            User patient,
            Doctor doctor,
            LocalDate appointmentDate,
            AppointmentStatus status
    );

    boolean existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatus(
            Doctor doctor,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            AppointmentStatus status
    );

    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);
    Page<Appointment> findAll(Pageable pageable);
    long countByStatus(AppointmentStatus status);

    // Dashboard queries
    List<Appointment> findByPatientOrderByAppointmentDateDesc(User patient);
    List<Appointment> findByPatientAndStatusOrderByAppointmentDateDesc(User patient, AppointmentStatus status);
    List<Appointment> findByPatientAndAppointmentDateAfterOrderByAppointmentDateAsc(User patient, LocalDate date);
    long countByPatient(User patient);
    long countByPatientAndStatus(User patient, AppointmentStatus status);

    List<Appointment> findByDoctorAndAppointmentDateOrderByAppointmentTimeAsc(Doctor doctor, LocalDate date);
    List<Appointment> findByDoctorAndAppointmentDateAfterOrderByAppointmentDateAsc(Doctor doctor, LocalDate date);
    long countByDoctor(Doctor doctor);
    long countByDoctorAndAppointmentDateBetween(Doctor doctor, LocalDate startDate, LocalDate endDate);

    // Admin analytics
    long countByAppointmentDateBetween(LocalDate startDate, LocalDate endDate);
    List<Appointment> findTop10ByOrderByIdDesc();
}

