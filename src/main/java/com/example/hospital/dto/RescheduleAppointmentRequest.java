package com.example.hospital.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class RescheduleAppointmentRequest {

    @NotNull(message = "New appointment date is required")
    private LocalDate newAppointmentDate;

    @NotNull(message = "New appointment time is required")
    private LocalTime newAppointmentTime;
}
