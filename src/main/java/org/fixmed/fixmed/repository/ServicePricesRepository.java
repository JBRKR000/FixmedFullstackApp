package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.ServicePrices;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicePricesRepository extends JpaRepository<ServicePrices, Long> {
    List<ServicePrices> findByDoctorFacilityAssignmentsId(Long assignmentId);

    @Query("SELECT sp FROM ServicePrices sp WHERE sp.doctorFacilityAssignments.doctor.id = :doctorId")
    List<ServicePrices> findByDoctorFacilityAssignments_Doctor_Id(Long doctorId);

    List<ServicePrices> findByFacilityMedicalServices_Id(Long facilityMedicalServicesId);

}
