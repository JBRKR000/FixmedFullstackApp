package org.fixmed.fixmed.service;

import org.fixmed.fixmed.model.ServicePrices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ServicePricesService {
    ServicePrices saveServicePrice(ServicePrices servicePrice);
    Optional<ServicePrices> getServicePriceById(Long id);
    List<ServicePrices> getServicePricesByAssignmentId(Long assignmentId);
    ServicePrices updateServicePrice(Long id, ServicePrices servicePrice);
    List<ServicePrices> getServicePricesByDoctorId(Long doctorId);
}
