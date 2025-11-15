package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;

import org.fixmed.fixmed.model.DoctorFacilityAssignments;
import org.fixmed.fixmed.model.Doctors;
import org.fixmed.fixmed.model.MedicalServices;
import org.fixmed.fixmed.model.ServicePrices;
import org.fixmed.fixmed.model.dto.CreateOrUpdateServiceRequest;
import org.fixmed.fixmed.model.dto.FacilityServiceCreateDto;
import org.fixmed.fixmed.repository.DoctorFacilityAssignmentsRepository;
import org.fixmed.fixmed.repository.DoctorsRepository;
import org.fixmed.fixmed.repository.ServicePricesRepository;
import org.fixmed.fixmed.service.MedicalServicesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class MedicalServicesController {
    private final MedicalServicesService service;
    private final ServicePricesRepository servicePricesRepository;
    private final DoctorFacilityAssignmentsRepository assignmentsRepository;
    private final DoctorsRepository doctorsRepository;
    @PostMapping("/facility")
    public ResponseEntity<?> createServiceForFacility(@RequestBody CreateOrUpdateServiceRequest req) {
        MedicalServices ms = new MedicalServices();
        ms.setName(req.getName());
        ms.setDescription(req.getDescription());

        if (req.getDoctorId() != null) {
            Doctors doctor = doctorsRepository.findById(req.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
            ms.setDoctor(doctor);
        }

        MedicalServices saved = service.saveMedicalService(ms);
        DoctorFacilityAssignments assignment = assignmentsRepository.findById(req.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        ServicePrices sp = new ServicePrices();
        sp.setMedicalServices(saved);
        sp.setDoctorFacilityAssignments(assignment);
        sp.setPrice(req.getPrice());
        sp.setDuration_time(req.getDurationMinutes());
        servicePricesRepository.save(sp);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    @PutMapping("/facility/{serviceId}")
    public ResponseEntity<?> updateServiceForFacility(
            @PathVariable Long serviceId,
            @RequestBody CreateOrUpdateServiceRequest req) {
        MedicalServices ms = service.getMedicalServiceById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        ms.setName(req.getName());
        ms.setDescription(req.getDescription());
        service.saveMedicalService(ms);
        ServicePrices sp = servicePricesRepository
                .findByDoctorFacilityAssignmentsId(req.getAssignmentId())
                .stream()
                .filter(p -> p.getMedicalServices().getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ServicePrice not found"));
        sp.setPrice(req.getPrice());
        sp.setDuration_time(req.getDurationMinutes());
        servicePricesRepository.save(sp);

        return ResponseEntity.ok(ms);
    }

    @DeleteMapping("/facility/{serviceId}")
    public ResponseEntity<?> deleteServiceForFacility(
            @PathVariable Long serviceId,
            @RequestParam Long assignmentId) {
        ServicePrices sp = servicePricesRepository
                .findByDoctorFacilityAssignmentsId(assignmentId)
                .stream()
                .filter(p -> p.getMedicalServices().getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("ServicePrice not found"));
        servicePricesRepository.delete(sp);
        boolean stillUsed = servicePricesRepository
                .findAll()
                .stream()
                .anyMatch(p -> p.getMedicalServices().getId().equals(serviceId));
        if (!stillUsed) {
            service.getMedicalServiceById(serviceId).ifPresent(s -> service.saveMedicalService(s));
        }
        return ResponseEntity.noContent().build();
    }

}
