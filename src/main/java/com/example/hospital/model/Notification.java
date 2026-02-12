package com.example.hospital.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String subject;

    @Column(length = 1000)
    private String message;

    private LocalDateTime sentAt;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private Long relatedAppointmentId;
}
