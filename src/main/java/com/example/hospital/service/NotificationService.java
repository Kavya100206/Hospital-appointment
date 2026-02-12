package com.example.hospital.service;

import com.example.hospital.model.Notification;
import com.example.hospital.model.NotificationStatus;
import com.example.hospital.model.NotificationType;
import com.example.hospital.model.User;
import com.example.hospital.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Async
    public void sendNotification(
            User user,
            NotificationType type,
            String subject,
            String message,
            Long relatedAppointmentId
    ) {
        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .subject(subject)
                .message(message)
                .sentAt(LocalDateTime.now())
                .status(NotificationStatus.PENDING)
                .relatedAppointmentId(relatedAppointmentId)
                .build();

        try {
            emailService.sendEmail(user.getEmail(), subject, message);
            notification.setStatus(NotificationStatus.SENT);
            log.info("Notification sent successfully to {}", user.getEmail());
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            log.error("Failed to send notification to {}: {}", user.getEmail(), e.getMessage());
        }

        notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(User user) {
        return notificationRepository.findByUserOrderBySentAtDesc(user);
    }

    public long getUnreadNotificationCount(User user) {
        return notificationRepository.countByUserAndStatus(user, NotificationStatus.SENT);
    }
}
