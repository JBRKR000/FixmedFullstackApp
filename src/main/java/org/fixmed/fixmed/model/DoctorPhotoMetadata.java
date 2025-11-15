package org.fixmed.fixmed.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctor_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorPhotoMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false, unique = true)
    private Doctors doctor;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String checksum;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileType type;
}