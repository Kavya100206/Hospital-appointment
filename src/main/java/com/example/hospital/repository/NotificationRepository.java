package com.example.hospital.repository;

import com.example.hospital.model.Notification;
import com.example.hospital.model.NotificationStatus;
import com.example.hospital.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserOrderBySentAtDesc(User user);
    List<Notification> findByStatusAndSentAtBefore(NotificationStatus status, LocalDateTime dateTime);
    long countByUserAndStatus(User user, NotificationStatus status);
}
