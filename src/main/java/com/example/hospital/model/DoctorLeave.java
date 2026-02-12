package com.example.hospital.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor is required")
    private Doctor doctor;

    @NotNull(message = "Leave date is required")
    private LocalDate leaveDate;

    @NotBlank(message = "Reason is required")
    private String reason;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Leave type is required")
    private LeaveType leaveType;
}
