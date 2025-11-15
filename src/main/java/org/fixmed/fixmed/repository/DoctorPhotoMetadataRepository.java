package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.DoctorPhotoMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorPhotoMetadataRepository extends JpaRepository<DoctorPhotoMetadata, Long> {
    Optional<DoctorPhotoMetadata> findByDoctor_Id(Long doctorId);
}