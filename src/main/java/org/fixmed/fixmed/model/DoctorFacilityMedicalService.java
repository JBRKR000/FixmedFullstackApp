package org.fixmed.fixmed.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctor_facility_medical_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorFacilityMedicalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctors doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_medical_service_id", nullable = false)
    private FacilityMedicalServices facilityMedicalService;
}