package org.fixmed.fixmed.service.impl;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.ServicePrices;
import org.fixmed.fixmed.repository.ServicePricesRepository;
import org.fixmed.fixmed.service.ServicePricesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServicePricesServiceImpl implements ServicePricesService {
    private final ServicePricesRepository repository;


    @Override
    public ServicePrices saveServicePrice(ServicePrices servicePrice) {
        return repository.save(servicePrice);
    }

    @Override
    public Optional<ServicePrices> getServicePriceById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<ServicePrices> getServicePricesByAssignmentId(Long assignmentId) {
        return repository.findByDoctorFacilityAssignmentsId(assignmentId);
    }

    @Override
    public ServicePrices updateServicePrice(Long id, ServicePrices servicePrice) {
        return repository.findById(id)
                .map(existingServicePrice -> {
                    existingServicePrice.setPrice(servicePrice.getPrice());
                    existingServicePrice.setDuration_time(servicePrice.getDuration_time());
                    existingServicePrice.setMedicalServices(servicePrice.getMedicalServices());
                    existingServicePrice.setDoctorFacilityAssignments(servicePrice.getDoctorFacilityAssignments());
                    return repository.save(existingServicePrice);
                }).orElseThrow(() -> new RuntimeException("Service Price not found"));
    }
    @Override
    public List<ServicePrices> getServicePricesByDoctorId(Long doctorId) {
        return repository.findByDoctorFacilityAssignments_Doctor_Id(doctorId);
    }
}
