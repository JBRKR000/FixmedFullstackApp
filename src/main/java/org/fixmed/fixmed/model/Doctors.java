package org.fixmed.fixmed.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "doctors")
@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Doctors {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "license_number", nullable = false, length = 100)
    private String license_number;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phone_number;

    @Column(name = "city", length = 255)
    private String city;

    @NonNull
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<MedicalServices> services;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoctorFacilityMedicalService> doctorFacilityMedicalServices;

    @Column(name = "specialization", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private Specialization specialization;
    public enum Specialization {
        KARDIOLOG,
        DERMATOLOG,
        PEDIATRA,
        NEUROLOG,
        ORTOPEDA,
        PSYCHIATRA,
        RADIOLOG,
        ONKOLOG,
        GINEKOLOG,
        UROLOG,
        OKULISTA,
        MEDYCYNA_RODZINNA,
        OTHER
    }


}