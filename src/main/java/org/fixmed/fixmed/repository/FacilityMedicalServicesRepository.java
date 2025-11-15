package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.FacilityMedicalServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityMedicalServicesRepository extends JpaRepository<FacilityMedicalServices, Long> {
    List<FacilityMedicalServices> findByFacilityId(Long facilityId);
    List<FacilityMedicalServices> findByNameContainingIgnoreCase(String name);
    List<FacilityMedicalServices> findByFacilityIdAndNameContainingIgnoreCase(Long facilityId, String name);
}