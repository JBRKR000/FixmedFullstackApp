package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.DoctorFacilityMedicalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorFacilityMedicalServiceRepository extends JpaRepository<DoctorFacilityMedicalService, Long> {
    List<DoctorFacilityMedicalService> findByFacilityMedicalServiceId(Long facilityMedicalServiceId);
    List<DoctorFacilityMedicalService> findByDoctor_IdAndFacilityMedicalService_Facility_Id(Long doctorId, Long facilityId);
}