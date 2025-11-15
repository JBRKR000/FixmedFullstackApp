package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.DoctorFacilityMedicalService;
import org.fixmed.fixmed.repository.DoctorFacilityMedicalServiceRepository;
import org.fixmed.fixmed.repository.DoctorsRepository;
import org.fixmed.fixmed.repository.FacilityMedicalServicesRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctor-facility-medical-services")
@RequiredArgsConstructor
public class DoctorFacilityMedicalServiceController {

    private final DoctorFacilityMedicalServiceRepository repository;
    private final DoctorsRepository doctorsRepository;
    private final FacilityMedicalServicesRepository facilityMedicalServicesRepository;
    @PostMapping
    public ResponseEntity<?> assignDoctorToFacilityMedicalService(
            @RequestParam Long doctorId,
            @RequestParam Long facilityMedicalServiceId) {

        var doctorOpt = doctorsRepository.findById(doctorId);
        var serviceOpt = facilityMedicalServicesRepository.findById(facilityMedicalServiceId);

        if (doctorOpt.isEmpty() || serviceOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid doctorId or facilityMedicalServiceId");
        }

        DoctorFacilityMedicalService assignment = new DoctorFacilityMedicalService();
        assignment.setDoctor(doctorOpt.get());
        assignment.setFacilityMedicalService(serviceOpt.get());
        repository.save(assignment);

        return ResponseEntity.ok("Doctor assigned to facility medical service");
    }
}