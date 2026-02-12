package com.example.hospital.dto;

import com.example.hospital.model.DayOfWeek;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

@Data
public class DoctorAvailabilityRequest {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Min(value = 15, message = "Slot duration must be at least 15 minutes")
    private int slotDuration = 30;
}
