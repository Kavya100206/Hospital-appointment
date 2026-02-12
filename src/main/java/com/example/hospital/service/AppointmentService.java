package com.example.hospital.service;

import com.example.hospital.dto.AppointmentRequest;
import com.example.hospital.model.*;
import com.example.hospital.repository.AppointmentRepository;
import com.example.hospital.repository.DoctorRepository;
import com.example.hospital.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public Appointment bookAppointment(
            AppointmentRequest request,
            String patientEmail
    ) {

        User patient = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Check if the specific time slot is already booked
        boolean slotTaken = appointmentRepository.existsByDoctorAndAppointmentDateAndAppointmentTimeAndStatus(
                doctor,
                request.getAppointmentDate(),
                request.getAppointmentTime(),
                AppointmentStatus.BOOKED
        );

        if (slotTaken) {
            throw new RuntimeException("This time slot is already booked");
        }

        // Check if patient already has an appointment with this doctor on this date
        boolean alreadyBooked =
                appointmentRepository.existsByPatientAndDoctorAndAppointmentDateAndStatus(
                        patient,
                        doctor,
                        request.getAppointmentDate(),
                        AppointmentStatus.BOOKED
                );

        if (alreadyBooked) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You already have an active appointment with this doctor on this date"
            );
        }



        Appointment appointment = Appointment.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .status(AppointmentStatus.BOOKED)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        emailService.sendEmail(
                savedAppointment.getPatient().getEmail(),
                "Appointment Confirmed",
                "Your appointment with Dr. "
                        + savedAppointment.getDoctor().getName()
                        + " on "
                        + savedAppointment.getAppointmentDate()
                        + " at "
                        + savedAppointment.getAppointmentTime()
                        + " has been confirmed."
        );

        return savedAppointment;

    }
    public Appointment cancelAppointment(Long appointmentId, String patientEmail) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getPatient().getEmail().equals(patientEmail)) {
            throw new RuntimeException("You cannot cancel this appointment");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);

        // 🔔 EMAIL ON CANCELLATION
        emailService.sendEmail(
                appointment.getPatient().getEmail(),
                "Appointment Cancelled",
                "Your appointment with Dr. "
                        + appointment.getDoctor().getName()
                        + " on "
                        + appointment.getAppointmentDate()
                        + " at "
                        + appointment.getAppointmentTime()
                        + " has been cancelled."
        );

        return saved;
    }


    public List<Appointment> getAppointmentsForDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorIdAndStatus(doctorId, AppointmentStatus.BOOKED);
    }

    public Page<Appointment> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }

    public long countAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.countByStatus(status);
    }

    public Appointment rescheduleAppointment(
            Long appointmentId,
            String patientEmail,
            java.time.LocalDate newDate,
            java.time.LocalTime newTime
    ) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!appointment.getPatient().getEmail().equals(patientEmail)) {
            throw new RuntimeException("You cannot reschedule this appointment");
        }

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new RuntimeException("Only booked appointments can be rescheduled");
        }

        if (appointment.getRescheduleCount() >= 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Maximum reschedule limit (2) reached for this appointment"
            );
        }

        // Check if new slot is available
        boolean slotTaken = appointmentRepository.existsByPatientAndDoctorAndAppointmentDateAndStatus(
                appointment.getPatient(),
                appointment.getDoctor(),
                newDate,
                AppointmentStatus.BOOKED
        );

        if (slotTaken) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "You already have an appointment with this doctor on the new date"
            );
        }

        appointment.setAppointmentDate(newDate);
        appointment.setAppointmentTime(newTime);
        appointment.setRescheduleCount(appointment.getRescheduleCount() + 1);

        Appointment saved = appointmentRepository.save(appointment);

        // Send email notification
        emailService.sendEmail(
                appointment.getPatient().getEmail(),
                "Appointment Rescheduled",
                "Your appointment with Dr. " + appointment.getDoctor().getName()
                        + " has been rescheduled to " + newDate + " at " + newTime
        );

        return saved;
    }


}

