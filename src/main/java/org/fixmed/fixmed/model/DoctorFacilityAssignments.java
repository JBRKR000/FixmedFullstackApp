package org.fixmed.fixmed.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "doctor_facility_assignments")
@RequiredArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DoctorFacilityAssignments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctors doctor;

    @NonNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facilities facility;

    @Enumerated(EnumType.STRING)
    private Source source;

    @Column(name = "room_number", length = 50)
    private String roomNumber;
    
    @ManyToOne
    @JoinColumn(name = "facility_medical_service_id")
    private FacilityMedicalServices facilityMedicalService;

    public enum Source {
        LEKARZ, PLACÓWKA
    }
}
