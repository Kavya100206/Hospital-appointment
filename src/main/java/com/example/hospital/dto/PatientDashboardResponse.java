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
public class PatientDashboardResponse {
    private long totalAppointments;
    private long upcomingAppointments;
    private long completedAppointments;
    private long cancelledAppointments;
    private List<Appointment> nextAppointments;
    private Map<String, Long> favoriteDoctors; // Doctor name -> appointment count
}
