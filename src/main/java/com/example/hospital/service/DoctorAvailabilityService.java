package com.example.hospital.service;

import com.example.hospital.dto.AvailableSlotResponse;
import com.example.hospital.dto.DoctorAvailabilityRequest;
import com.example.hospital.dto.DoctorLeaveRequest;
import com.example.hospital.model.*;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorAvailabilityRepository;
import com.example.hospital.repository.DoctorLeaveRepository;
import com.example.hospital.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorAvailabilityService {

    private final DoctorRepository doctorRepository;
    private final DoctorAvailabilityRepository availabilityRepository;
    private final DoctorLeaveRepository leaveRepository;
    private final AppointmentRepository appointmentRepository;

    public DoctorAvailability setAvailability(Long doctorId, DoctorAvailabilityRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorAvailability availability = DoctorAvailability.builder()
                .doctor(doctor)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .slotDuration(request.getSlotDuration())
                .isActive(true)
                .build();

        return availabilityRepository.save(availability);
    }

    public List<DoctorAvailability> getDoctorAvailability(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return availabilityRepository.findByDoctorAndIsActiveTrue(doctor);
    }

    public DoctorLeave addLeave(Long doctorId, DoctorLeaveRequest request) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (leaveRepository.existsByDoctorAndLeaveDate(doctor, request.getLeaveDate())) {
            throw new RuntimeException("Leave already exists for this date");
        }

        DoctorLeave leave = DoctorLeave.builder()
                .doctor(doctor)
                .leaveDate(request.getLeaveDate())
                .reason(request.getReason())
                .leaveType(request.getLeaveType())
                .build();

        return leaveRepository.save(leave);
    }

    public List<DoctorLeave> getDoctorLeaves(Long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        return leaveRepository.findByDoctor(doctor);
    }

    public List<AvailableSlotResponse> getAvailableSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Check if doctor is on leave
        if (leaveRepository.existsByDoctorAndLeaveDate(doctor, date)) {
            return new ArrayList<>(); // No slots available on leave days
        }

        // Get day of week from date
        DayOfWeek dayOfWeek = convertToDayOfWeek(date.getDayOfWeek());

        // Get availability for this day
        List<DoctorAvailability> availabilities = availabilityRepository
                .findByDoctorAndDayOfWeekAndIsActiveTrue(doctor, dayOfWeek);

        if (availabilities.isEmpty()) {
            return new ArrayList<>();
        }

        List<AvailableSlotResponse> slots = new ArrayList<>();
        DoctorAvailability availability = availabilities.get(0);

        // Generate time slots
        LocalTime currentSlot = availability.getStartTime();
        LocalTime endTime = availability.getEndTime();

        while (currentSlot.isBefore(endTime)) {
            boolean isBooked = appointmentRepository.existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatus(
                    doctor,
                    date,
                    currentSlot,
                    AppointmentStatus.BOOKED
            );

            slots.add(AvailableSlotResponse.builder()
                    .slotTime(currentSlot)
                    .isAvailable(!isBooked)
                    .build());

            currentSlot = currentSlot.plusMinutes(availability.getSlotDuration());
        }

        return slots;
    }

    private DayOfWeek convertToDayOfWeek(java.time.DayOfWeek javaDayOfWeek) {
        return DayOfWeek.valueOf(javaDayOfWeek.name());
    }
}
