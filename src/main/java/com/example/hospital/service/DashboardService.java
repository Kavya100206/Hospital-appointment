package com.example.hospital.service;

import com.example.hospital.dto.AdminDashboardResponse;
import com.example.hospital.dto.DoctorDashboardResponse;
import com.example.hospital.dto.PatientDashboardResponse;
import com.example.hospital.model.*;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;

    @Cacheable(value = "patientDashboard", key = "#patientEmail")
    public PatientDashboardResponse getPatientDashboard(String patientEmail) {
        User patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        long totalAppointments = appointmentRepository.countByPatient(patient);
        long upcomingAppointments = appointmentRepository
                .findByPatientAndAppointmentDateAfterOrderByAppointmentDateAsc(patient, LocalDate.now())
                .size();
        long completedAppointments = appointmentRepository
                .countByPatientAndStatus(patient, AppointmentStatus.COMPLETED);
        long cancelledAppointments = appointmentRepository
                .countByPatientAndStatus(patient, AppointmentStatus.CANCELLED);

        List<Appointment> nextAppointments = appointmentRepository
                .findByPatientAndAppointmentDateAfterOrderByAppointmentDateAsc(patient, LocalDate.now())
                .stream()
                .limit(5)
                .collect(Collectors.toList());

        // Calculate favorite doctors (most visited)
        Map<String, Long> favoriteDoctors = appointmentRepository
                .findByPatientOrderByAppointmentDateDesc(patient)
                .stream()
                .collect(Collectors.groupingBy(
                        appointment -> appointment.getDoctor().getName(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        HashMap::new
                ));

        return PatientDashboardResponse.builder()
                .totalAppointments(totalAppointments)
                .upcomingAppointments(upcomingAppointments)
                .completedAppointments(completedAppointments)
                .cancelledAppointments(cancelledAppointments)
                .nextAppointments(nextAppointments)
                .favoriteDoctors(favoriteDoctors)
                .build();
    }

    @Cacheable(value = "doctorDashboard", key = "#doctorEmail")
    public DoctorDashboardResponse getDoctorDashboard(String doctorEmail) {
        User user = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Find doctor by email (assuming doctor has a user account)
        // For simplicity, we'll find by name match or you can add email to Doctor entity
        List<Doctor> doctors = doctorRepository.findAll();
        Doctor doctor = doctors.stream()
                .filter(d -> d.getName().equalsIgnoreCase(user.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate weekEnd = weekStart.plusDays(6);

        List<Appointment> todaySchedule = appointmentRepository
                .findByDoctorAndAppointmentDateOrderByAppointmentTimeAsc(doctor, today);

        long todayAppointmentsCount = todaySchedule.size();

        long thisWeekAppointmentsCount = appointmentRepository
                .countByDoctorAndAppointmentDateBetween(doctor, weekStart, weekEnd);

        long totalPatientsServed = appointmentRepository.countByDoctor(doctor);

        List<Appointment> upcomingAppointments = appointmentRepository
                .findByDoctorAndAppointmentDateAfterOrderByAppointmentDateAsc(doctor, today)
                .stream()
                .limit(10)
                .collect(Collectors.toList());

        return DoctorDashboardResponse.builder()
                .todayAppointmentsCount(todayAppointmentsCount)
                .thisWeekAppointmentsCount(thisWeekAppointmentsCount)
                .totalPatientsServed(totalPatientsServed)
                .todaySchedule(todaySchedule)
                .upcomingAppointments(upcomingAppointments)
                .build();
    }

    @Cacheable(value = "adminDashboard")
    public AdminDashboardResponse getAdminDashboard() {
        long totalUsers = userRepository.count();
        long totalPatients = userRepository.countByRole(Role.PATIENT);
        long totalDoctors = userRepository.countByRole(Role.DOCTOR);
        long totalAdmins = userRepository.countByRole(Role.ADMIN);
        long totalAppointments = appointmentRepository.count();

        // Appointments by status
        Map<String, Long> appointmentsByStatus = new HashMap<>();
        for (AppointmentStatus status : AppointmentStatus.values()) {
            appointmentsByStatus.put(status.name(), appointmentRepository.countByStatus(status));
        }

        // Popular specializations
        Map<String, Long> popularSpecializations = doctorRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        Doctor::getSpecialization,
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        HashMap::new
                ));

        // Recent appointments
        List<Appointment> recentAppointments = appointmentRepository
                .findTop10ByOrderByIdDesc();

        // This month vs last month
        LocalDate now = LocalDate.now();
        LocalDate thisMonthStart = now.withDayOfMonth(1);
        LocalDate thisMonthEnd = now.withDayOfMonth(now.lengthOfMonth());
        LocalDate lastMonthStart = thisMonthStart.minusMonths(1);
        LocalDate lastMonthEnd = lastMonthStart.withDayOfMonth(lastMonthStart.lengthOfMonth());

        long thisMonthAppointments = appointmentRepository
                .countByAppointmentDateBetween(thisMonthStart, thisMonthEnd);
        long lastMonthAppointments = appointmentRepository
                .countByAppointmentDateBetween(lastMonthStart, lastMonthEnd);

        return AdminDashboardResponse.builder()
                .totalUsers(totalUsers)
                .totalPatients(totalPatients)
                .totalDoctors(totalDoctors)
                .totalAdmins(totalAdmins)
                .totalAppointments(totalAppointments)
                .appointmentsByStatus(appointmentsByStatus)
                .popularSpecializations(popularSpecializations)
                .recentAppointments(recentAppointments)
                .thisMonthAppointments(thisMonthAppointments)
                .lastMonthAppointments(lastMonthAppointments)
                .build();
    }
}
