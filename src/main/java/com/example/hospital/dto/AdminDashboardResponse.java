package com.example.hospital.dto;

import com.example.hospital.model.Appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponse {
    private long totalUsers;
    private long totalPatients;
    private long totalDoctors;
    private long totalAdmins;
    private long totalAppointments;
    private Map<String, Long> appointmentsByStatus; // Status -> count
    private Map<String, Long> popularSpecializations; // Specialization -> count
    private List<Appointment> recentAppointments;
    private long thisMonthAppointments;
    private long lastMonthAppointments;
}
