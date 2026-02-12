package com.example.hospital.service;

import com.example.hospital.model.Appointment;
import com.example.hospital.model.AppointmentStatus;
import com.example.hospital.model.NotificationType;
import com.example.hospital.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentReminderService {

    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;

    // Run every hour
    @Scheduled(cron = "0 0 * * * *")
    public void send24HourReminders() {
        log.info("Running 24-hour appointment reminder job");

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Appointment> appointments = appointmentRepository.findAll()
                .stream()
                .filter(apt -> apt.getAppointmentDate().equals(tomorrow))
                .filter(apt -> apt.getStatus() == AppointmentStatus.BOOKED)
                .toList();

        for (Appointment appointment : appointments) {
            String message = String.format(
                    "Reminder: You have an appointment with Dr. %s tomorrow (%s) at %s. " +
                            "Please arrive 10 minutes early.",
                    appointment.getDoctor().getName(),
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime()
            );

            notificationService.sendNotification(
                    appointment.getPatient(),
                    NotificationType.APPOINTMENT_REMINDER_24H,
                    "Appointment Reminder - Tomorrow",
                    message,
                    appointment.getId()
            );
        }

        log.info("Sent {} 24-hour reminders", appointments.size());
    }

    // Run every 30 minutes
    @Scheduled(cron = "0 */30 * * * *")
    public void send1HourReminders() {
        log.info("Running 1-hour appointment reminder job");

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        List<Appointment> appointments = appointmentRepository.findAll()
                .stream()
                .filter(apt -> {
                    LocalDateTime appointmentDateTime = LocalDateTime.of(
                            apt.getAppointmentDate(),
                            apt.getAppointmentTime()
                    );
                    return appointmentDateTime.isAfter(now) &&
                            appointmentDateTime.isBefore(oneHourLater) &&
                            apt.getStatus() == AppointmentStatus.BOOKED;
                })
                .toList();

        for (Appointment appointment : appointments) {
            String message = String.format(
                    "Reminder: Your appointment with Dr. %s is in 1 hour at %s. " +
                            "Please be on time.",
                    appointment.getDoctor().getName(),
                    appointment.getAppointmentTime()
            );

            notificationService.sendNotification(
                    appointment.getPatient(),
                    NotificationType.APPOINTMENT_REMINDER_1H,
                    "Appointment Reminder - 1 Hour",
                    message,
                    appointment.getId()
            );
        }

        log.info("Sent {} 1-hour reminders", appointments.size());
    }
}
