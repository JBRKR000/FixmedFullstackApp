package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.ServicePrices;
import org.fixmed.fixmed.model.dto.ServicePriceDto;
import org.fixmed.fixmed.service.ServicePricesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-prices")
@RequiredArgsConstructor
public class ServicePricesController {
    private final ServicePricesService service;


    @PostMapping
    public ResponseEntity<ServicePrices> createServicePrice(@RequestBody ServicePrices servicePrice) {
        return ResponseEntity.ok(service.saveServicePrice(servicePrice));
    }

    @GetMapping
    public ResponseEntity<List<ServicePrices>> getServicePricesByAssignmentId(@RequestParam Long assignmentId) {
        return ResponseEntity.ok(service.getServicePricesByAssignmentId(assignmentId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicePrices> updateServicePrice(@PathVariable Long id, @RequestBody ServicePrices servicePrice) {
        return ResponseEntity.ok(service.updateServicePrice(id, servicePrice));
    }
    @GetMapping("/by-doctor")
    public ResponseEntity<List<ServicePriceDto>> getServicePricesByDoctorId(@RequestParam Long doctorId) {
        List<ServicePrices> prices = service.getServicePricesByDoctorId(doctorId);
        List<ServicePriceDto> dtos = prices.stream()
            .map(sp -> new ServicePriceDto(
                sp.getMedicalServices().getId(),
                sp.getMedicalServices().getName(),
                sp.getPrice(),
                sp.getDuration_time()
            ))
            .toList();
        return ResponseEntity.ok(dtos);
    }
}
