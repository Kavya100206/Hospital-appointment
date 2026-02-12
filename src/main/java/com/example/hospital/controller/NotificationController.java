package com.example.hospital.controller;

import com.example.hospital.model.Notification;
import com.example.hospital.model.User;
import com.example.hospital.repository.UserRepository;
import com.example.hospital.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @GetMapping
    public List<Notification> getMyNotifications(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationService.getUserNotifications(user);
    }

    @GetMapping("/unread-count")
    public long getUnreadCount(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationService.getUnreadNotificationCount(user);
    }
}
