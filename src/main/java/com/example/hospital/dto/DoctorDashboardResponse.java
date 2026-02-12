package com.example.hospital.dto;

import com.example.hospital.model.Appointment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDashboardResponse {
    private long todayAppointmentsCount;
    private long thisWeekAppointmentsCount;
    private long totalPatientsServed;
    private List<Appointment> todaySchedule;
    private List<Appointment> upcomingAppointments;
}
