package org.fixmed.fixmed.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "facility_medical_services")
@RequiredArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacilityMedicalServices {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Facilities facility;

    @OneToMany(mappedBy = "facilityMedicalService", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorFacilityMedicalService> doctorFacilityMedicalServices;
}