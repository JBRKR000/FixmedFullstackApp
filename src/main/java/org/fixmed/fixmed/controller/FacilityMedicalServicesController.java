package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;

import org.fixmed.fixmed.model.DoctorFacilityAssignments;
import org.fixmed.fixmed.model.FacilityMedicalServices;
import org.fixmed.fixmed.model.ServicePrices;
import org.fixmed.fixmed.model.dto.FacilityMedicalServiceDto;
import org.fixmed.fixmed.model.dto.FacilityServiceCreateDto;
import org.fixmed.fixmed.repository.DoctorFacilityAssignmentsRepository;
import org.fixmed.fixmed.repository.FacilitiesRepository;
import org.fixmed.fixmed.repository.ServicePricesRepository;
import org.fixmed.fixmed.service.FacilityMedicalServicesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("facility-services")
@RequiredArgsConstructor
public class FacilityMedicalServicesController {

    private final FacilityMedicalServicesService service;
    private final DoctorFacilityAssignmentsRepository assignmentsRepository;
    private final ServicePricesRepository servicePricesRepository;
    private final FacilityMedicalServicesService facilityMedicalServicesService;
    private final FacilitiesRepository facilitiesRepository;

    @PostMapping
    public ResponseEntity<FacilityMedicalServices> create(
            @RequestBody FacilityMedicalServices facilityMedicalServices) {
        return ResponseEntity.ok(service.save(facilityMedicalServices));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacilityMedicalServices> getById(@PathVariable Long id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/facility/{facilityId}")
    public ResponseEntity<List<FacilityMedicalServiceDto>> getByFacilityId(@PathVariable Long facilityId) {
        List<FacilityMedicalServices> services = service.getByFacilityId(facilityId);
        List<FacilityMedicalServiceDto> dtos = services.stream().map(fms -> {
            FacilityMedicalServiceDto dto = new FacilityMedicalServiceDto();
            dto.setId(fms.getId());
            dto.setName(fms.getName());
            dto.setDescription(fms.getDescription());
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping
    public ResponseEntity<List<FacilityMedicalServices>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        List<ServicePrices> prices = servicePricesRepository.findByFacilityMedicalServices_Id(id);
        servicePricesRepository.deleteAll(prices);
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/facility-medical-service")
    public ResponseEntity<?> createFacilityMedicalService(@RequestBody FacilityServiceCreateDto req) {
        // Sprawdź czy facilityId jest podane
        if (req.getFacilityId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("facilityId is required in the request body");
        }

        // Sprawdź czy placówka istnieje
        var facilityOpt = facilitiesRepository.findById(req.getFacilityId());
        if (facilityOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Facility not found");
        }

        FacilityMedicalServices fms = new FacilityMedicalServices();
        fms.setName(req.getName());
        fms.setDescription(req.getDescription());
        fms.setFacility(facilityOpt.get());
        FacilityMedicalServices saved = facilityMedicalServicesService.save(fms);

        ServicePrices sp = new ServicePrices();
        sp.setFacilityMedicalServices(saved);
        sp.setPrice(req.getPrice());
        sp.setDuration_time(req.getDurationMinutes());
        if (req.getAssignmentId() != null) {
            assignmentsRepository.findById(req.getAssignmentId()).ifPresent(sp::setDoctorFacilityAssignments);
        }
        servicePricesRepository.save(sp);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/facility-medical-service/{id}")
    public ResponseEntity<?> updateFacilityMedicalService(
            @PathVariable Long id,
            @RequestBody FacilityServiceCreateDto req) {
        var fmsOpt = facilityMedicalServicesService.getById(id);
        if (fmsOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Facility medical service not found");
        }
        FacilityMedicalServices fms = fmsOpt.get();
        fms.setName(req.getName());
        fms.setDescription(req.getDescription());
        FacilityMedicalServices updatedFms = facilityMedicalServicesService.save(fms);
        ServicePrices sp = servicePricesRepository
                .findByFacilityMedicalServices_Id(id)
                .stream()
                .findFirst()
                .orElse(null);
        if (sp != null) {
            sp.setPrice(req.getPrice());
            sp.setDuration_time(req.getDurationMinutes());
            servicePricesRepository.save(sp);
        }
        return ResponseEntity.ok(updatedFms);
    }

    @GetMapping("/facility-medical-service/{id}")
    public ResponseEntity<?> getFacilityMedicalServiceDetails(@PathVariable Long id) {
        var fmsOpt = facilityMedicalServicesService.getById(id);
        if (fmsOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Facility medical service not found");
        }
        FacilityMedicalServices fms = fmsOpt.get();
        ServicePrices sp = servicePricesRepository
                .findByFacilityMedicalServices_Id(id)
                .stream()
                .findFirst()
                .orElse(null);
        var response = new java.util.HashMap<String, Object>();
        response.put("name", fms.getName());
        response.put("description", fms.getDescription());
        response.put("duration", sp != null ? sp.getDuration_time() : null);
        response.put("price", sp != null ? sp.getPrice() : null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/facility/{facilityId}/services")
    public ResponseEntity<List<java.util.HashMap<String, Object>>> getAllFacilityMedicalServicesWithDetails(
            @PathVariable Long facilityId) {
        List<FacilityMedicalServices> services = facilityMedicalServicesService.getByFacilityId(facilityId);
        List<java.util.HashMap<String, Object>> result = services.stream().map(fms -> {
            ServicePrices sp = servicePricesRepository
                    .findByFacilityMedicalServices_Id(fms.getId())
                    .stream()
                    .findFirst()
                    .orElse(null);
            var map = new java.util.HashMap<String, Object>();
            map.put("id", fms.getId());
            map.put("name", fms.getName());
            map.put("description", fms.getDescription());
            map.put("duration", sp != null ? sp.getDuration_time() : null);
            map.put("price", sp != null ? sp.getPrice() : null);
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

}