package com.example.hospital.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"name", "specialization"})
})
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Doctor name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Specialization is required")
    @Column(nullable = false)
    private String specialization;

    @Min(value = 0, message = "Experience cannot be negative")
    private int experience; // years

    private boolean available;
}
