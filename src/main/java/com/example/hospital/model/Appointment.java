package com.example.hospital.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Patient (User)
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // Doctor
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private int rescheduleCount = 0;
}
