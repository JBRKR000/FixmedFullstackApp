package org.fixmed.fixmed.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Table(name = "appointments")
@RequiredArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Appointments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private DoctorFacilityAssignments assignment;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private MedicalServices service;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patients patient;

    @NonNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @NonNull
    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "patient_name", nullable = false)
    private String patientName;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;

    @Column(name = "prescribed_medications", columnDefinition = "TEXT")
    private String prescribedMedications;


    public enum AppointmentStatus {
        SCHEDULED, CANCELED, COMPLETED, CONFIRMED,

    }
}
