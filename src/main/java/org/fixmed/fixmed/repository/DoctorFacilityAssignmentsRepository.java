package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.DoctorFacilityAssignments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorFacilityAssignmentsRepository extends JpaRepository<DoctorFacilityAssignments, Long> {

    List<DoctorFacilityAssignments> findByDoctor_IdAndFacility_Id(Long doctorId, Long facilityId);

    List<DoctorFacilityAssignments> findByDoctor_Id(Long doctorId);

    List<DoctorFacilityAssignments> findByFacility_Id(Long facilityId);

    List<DoctorFacilityAssignments> findBySource(DoctorFacilityAssignments.Source source);

    List<DoctorFacilityAssignments> findByFacilityMedicalServiceId(Long facilityMedicalServiceId);
}

