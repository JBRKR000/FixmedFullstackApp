package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;

import org.fixmed.fixmed.model.Appointments;
import org.fixmed.fixmed.model.DoctorFacilityAssignments;
import org.fixmed.fixmed.model.DoctorFacilityMedicalService;
import org.fixmed.fixmed.model.Facilities;
import org.fixmed.fixmed.model.FacilityMedicalServices;
import org.fixmed.fixmed.model.MedicalServices;
import org.fixmed.fixmed.model.Patients;
import org.fixmed.fixmed.model.Receptionist;
import org.fixmed.fixmed.model.ServicePrices;
import org.fixmed.fixmed.model.dto.FacilityDoctorDto;
import org.fixmed.fixmed.model.dto.FacilityPublicProfileDto;
import org.fixmed.fixmed.model.dto.FacilityReviewDto;
import org.fixmed.fixmed.model.dto.FacilitySearchResult;
import org.fixmed.fixmed.model.dto.FacilityServiceDto;
import org.fixmed.fixmed.model.dto.PatientSimpleDto;
import org.fixmed.fixmed.model.dto.UpdateFacilityRequest;
import org.fixmed.fixmed.repository.DoctorFacilityMedicalServiceRepository;
import org.fixmed.fixmed.service.AppointmentsService;
import org.fixmed.fixmed.service.FacilitiesService;
import org.fixmed.fixmed.service.FacilityMedicalServicesService;
import org.fixmed.fixmed.service.MedicalServicesService;
import org.fixmed.fixmed.service.ReceptionistService;
import org.fixmed.fixmed.service.ServicePricesService;
import org.fixmed.fixmed.service.DoctorFacilityAssignmentsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.fixmed.fixmed.service.impl.FacilitiesServiceImpl;

@RestController
@RequestMapping("/facilities")
@RequiredArgsConstructor
public class FacilitiesController {
    private final FacilitiesService facilitiesService;
    private final AppointmentsService appointmentsService;
    private final ServicePricesService servicePricesService;
    private final DoctorFacilityAssignmentsService assignmentsService;
    private final ReceptionistService receptionistService;
    private final FacilityMedicalServicesService facilityMedicalServicesService;
    private final MedicalServicesService medicalServicesService;
    private final DoctorFacilityMedicalServiceRepository doctorFacilityMedicalServicesRepository; // Dodano wstrzyknięcie repozytorium

    @PostMapping
    public ResponseEntity<Facilities> createFacility(@RequestBody Facilities facility) {
        return ResponseEntity.ok(facilitiesService.saveFacility(facility));
    }

    @GetMapping
    public ResponseEntity<Page<Facilities>> getAllFacilities(Pageable pageable) {
        return ResponseEntity.ok(facilitiesService.getAllFacilities(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Facilities> updateFacility(
            @PathVariable Long id,
            @RequestBody UpdateFacilityRequest request) {
        Facilities updated = facilitiesService.getFacilityById(id)
                .map(facility -> {
                    facility.setName(request.getName());
                    facility.setEmail(request.getEmail());
                    facility.setAddress(request.getAddress());
                    return facilitiesService.saveFacility(facility);
                })
                .orElseThrow(() -> new RuntimeException("Facility not found"));
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/search")
    public ResponseEntity<List<FacilitySearchResult>> searchFacilities(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            Pageable pageable) {
        List<Facilities> facilities = facilitiesService.searchFacilities(name, address, Pageable.unpaged())
                .getContent();

        List<FacilitySearchResult> results = facilities.stream()
                .map(FacilitiesServiceImpl::mapToSearchResult)
                .toList();

        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}/patients")
    public ResponseEntity<List<PatientSimpleDto>> getPatientsByFacility(@PathVariable Long id) {
        List<Appointments> appointments = appointmentsService.getAllAppointments(Pageable.unpaged())
                .stream()
                .filter(a -> a.getAssignment().getFacility().getId().equals(id))
                .toList();
        List<PatientSimpleDto> patients = appointments.stream()
                .map(Appointments::getPatient)
                .distinct()
                .map(p -> new PatientSimpleDto(
                        p.getId(),
                        p.getUser().getFirst_name(),
                        p.getUser().getLast_name(),
                        p.getUser().getEmail(),
                        p.getPesel(),
                        p.getPhone_number()))
                .toList();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{id}/public-profile")
    public ResponseEntity<FacilityPublicProfileDto> getFacilityPublicProfile(@PathVariable Long id) {
        return facilitiesService.getFacilityById(id)
                .map(facility -> {
                    FacilityPublicProfileDto dto = new FacilityPublicProfileDto();
                    dto.setName(facility.getName());
                    String address = facility.getAddress();
                    String city = "";
                    if (address != null && address.contains(",")) {
                        String[] parts = address.split(",");
                        city = parts.length > 1 ? parts[1].trim() : "";
                    }
                    dto.setCity(city);
                    dto.setAddresses(List.of(address));

                    dto.setLogoUrl(null);
                    dto.setAverageRating(0.0);
                    dto.setNumberOfReviews(0);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/services")
    public ResponseEntity<List<FacilityServiceDto>> getFacilityServices(@PathVariable Long id) {
        List<ServicePrices> prices = servicePricesService.getServicePricesByAssignmentId(id);
        List<FacilityServiceDto> dtos = prices.stream()
                .map(sp -> {
                    FacilityServiceDto dto = new FacilityServiceDto();
                    dto.setServiceName(sp.getMedicalServices().getName());
                    dto.setPrice(sp.getPrice());
                    dto.setDurationMinutes(sp.getDuration_time());
                    return dto;
                })
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}/doctors")
    public ResponseEntity<List<FacilityDoctorDto>> getFacilityDoctors(@PathVariable Long id) {
        List<DoctorFacilityAssignments> assignments = assignmentsService.getAssignmentsByFacility(id);
        List<FacilityDoctorDto> doctors = assignments.stream()
                .map(DoctorFacilityAssignments::getDoctor)
                .distinct()
                .map(doctor -> FacilityDoctorDto.builder()
                        .doctorId(doctor.getId())
                        .fullName(doctor.getUser().getFirst_name() + " " + doctor.getUser().getLast_name())
                        .pwzNumber(doctor.getLicense_number())
                        .specializations(
                                doctor.getSpecialization() != null
                                        ? doctor.getSpecialization().name()
                                        : "")
                        .build())
                .toList();

        return ResponseEntity.ok(doctors);
    }

    
    @GetMapping("/{id}/receptionists")
    public ResponseEntity<List<Receptionist>> getFacilityReceptionists(@PathVariable Long id) {
        List<Receptionist> receptionists = receptionistService.getReceptionistsByFacility(id);
        return ResponseEntity.ok(receptionists);
    }

    @GetMapping("/receptionist/{userId}/facility")
    public ResponseEntity<Facilities> getFacilityByReceptionist(@PathVariable Long userId) {
        return receptionistService.getFacilityByReceptionistUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-assignment/{assignmentId}")
    public ResponseEntity<Facilities> getFacilityByAssignmentId(@PathVariable Long assignmentId) {
        return assignmentsService.getAssignmentById(assignmentId)
                .map(assignment -> ResponseEntity.ok(assignment.getFacility()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{facilityId}/assignment-ids")
    public ResponseEntity<List<Long>> getAssignmentIdsByFacilityId(@PathVariable Long facilityId) {
        List<Long> assignmentIds = assignmentsService.getAssignmentsByFacility(facilityId)
                .stream()
                .map(a -> a.getId())
                .toList();
        return ResponseEntity.ok(assignmentIds);
    }

    @GetMapping("/{facilityId}/services-with-doctors")
    public ResponseEntity<Map<String, List<FacilityDoctorDto>>> getServicesWithDoctors(@PathVariable Long facilityId) {
        Map<String, List<FacilityDoctorDto>> servicesWithDoctors = new HashMap<>();

        // Pobierz usługi z facility_medical_services
        List<FacilityMedicalServices> facilityServices = facilityMedicalServicesService.getByFacilityId(facilityId);
        for (FacilityMedicalServices service : facilityServices) {
            // Pobierz lekarzy przypisanych do konkretnej usługi placówki z tabeli doctor_facility_medical_services
            List<DoctorFacilityMedicalService> doctorMedicalServices = doctorFacilityMedicalServicesRepository.findByFacilityMedicalServiceId(service.getId());
            List<FacilityDoctorDto> doctors = doctorMedicalServices.stream()
                    .map(DoctorFacilityMedicalService::getDoctor)
                    .distinct()
                    .map(doctor -> FacilityDoctorDto.builder()
                            .doctorId(doctor.getId())
                            .fullName(doctor.getUser().getFirst_name() + " " + doctor.getUser().getLast_name())
                            .pwzNumber(doctor.getLicense_number())
                            .specializations(
                                    doctor.getSpecialization() != null
                                            ? doctor.getSpecialization().name()
                                            : "")
                            .build())
                    .toList();
            servicesWithDoctors.put(service.getName(), doctors);
        }

        // Pobierz usługi z medical_services
        List<MedicalServices> privateServices = medicalServicesService.getAllMedicalServices(Pageable.unpaged()).getContent();
        for (MedicalServices service : privateServices) {
            if (service.getDoctor() != null) {
                FacilityDoctorDto doctorDto = FacilityDoctorDto.builder()
                        .doctorId(service.getDoctor().getId())
                        .fullName(service.getDoctor().getUser().getFirst_name() + " " + service.getDoctor().getUser().getLast_name())
                        .pwzNumber(service.getDoctor().getLicense_number())
                        .specializations(
                                service.getDoctor().getSpecialization() != null
                                        ? service.getDoctor().getSpecialization().name()
                                        : "")
                        .build();
                servicesWithDoctors.computeIfAbsent(service.getName(), k -> new ArrayList<>()).add(doctorDto);
            }
        }

        return ResponseEntity.ok(servicesWithDoctors);
    }
}