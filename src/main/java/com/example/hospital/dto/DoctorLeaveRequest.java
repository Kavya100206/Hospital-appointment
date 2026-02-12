package com.example.hospital.dto;

import com.example.hospital.model.LeaveType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DoctorLeaveRequest {

    @NotNull(message = "Leave date is required")
    private LocalDate leaveDate;

    @NotBlank(message = "Reason is required")
    private String reason;

    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;
}
