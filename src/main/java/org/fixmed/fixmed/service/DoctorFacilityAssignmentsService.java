package org.fixmed.fixmed.service;
import org.fixmed.fixmed.model.DoctorFacilityAssignments;

import java.util.List;
import java.util.Optional;

public interface DoctorFacilityAssignmentsService {
   List<DoctorFacilityAssignments> getAllAssignments();
   Optional<DoctorFacilityAssignments> getAssignmentById(Long id);
   DoctorFacilityAssignments createAssignment(DoctorFacilityAssignments assignment);
   DoctorFacilityAssignments updateAssignment(Long id, DoctorFacilityAssignments assignment);
   void deleteAssignment(Long id);
   // Pobieranie przypisów dla lekarza i placówki
   List<DoctorFacilityAssignments> getAssignments(Long doctorId, Long facilityId);

   // Pobieranie przypisów dla lekarza
   List<DoctorFacilityAssignments> getAssignmentsByDoctor(Long doctorId);

   // Pobieranie przypisów dla placówki
   List<DoctorFacilityAssignments> getAssignmentsByFacility(Long facilityId);
   List<DoctorFacilityAssignments> getAssignmentsByFacilityMedicalService(Long facilityMedicalServiceId);
}
