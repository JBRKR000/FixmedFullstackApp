package org.fixmed.fixmed.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Entity
@Table(name="service_prices")
@RequiredArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServicePrices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private DoctorFacilityAssignments doctorFacilityAssignments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private MedicalServices medicalServices;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_medical_service_id")
    private FacilityMedicalServices facilityMedicalServices;

    @NonNull
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @NonNull
    @Column(nullable = false)
    private Integer duration_time;

}