package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.DoctorFacilityAssignments;
import org.fixmed.fixmed.model.Facilities;
import org.fixmed.fixmed.model.dto.DoctorFacilityAssignmentDto;
import org.fixmed.fixmed.model.dto.WeeklyScheduleDto;
import org.fixmed.fixmed.repository.DoctorFacilityMedicalServiceRepository;
import org.fixmed.fixmed.service.AvailabilitySlotsService;
import org.fixmed.fixmed.service.DoctorFacilityAssignmentsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/assignments")
@RequiredArgsConstructor
public class DoctorFacilityAssignmentsController {

    private final DoctorFacilityAssignmentsService assignmentsService;
    private final AvailabilitySlotsService availabilitySlotsService;
    private final DoctorFacilityMedicalServiceRepository doctorFacilityMedicalServiceRepository;

    @PostMapping
    public ResponseEntity<DoctorFacilityAssignments> createAssignment(
            @RequestBody DoctorFacilityAssignments assignment) {
        DoctorFacilityAssignments createdAssignment = assignmentsService.createAssignment(assignment);
        return ResponseEntity.ok(createdAssignment);
    }

    @GetMapping
    public ResponseEntity<List<DoctorFacilityAssignmentDto>> getAssignments(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) Long facilityId) {

        List<DoctorFacilityAssignments> assignments;
        if (doctorId != null && facilityId != null) {
            assignments = assignmentsService.getAssignments(doctorId, facilityId);
        } else if (doctorId != null) {
            assignments = assignmentsService.getAssignmentsByDoctor(doctorId);
        } else if (facilityId != null) {
            assignments = assignmentsService.getAssignmentsByFacility(facilityId);
        } else {
            assignments = List.of();
        }

        List<DoctorFacilityAssignmentDto> dtos = assignments.stream().map(a -> {
            DoctorFacilityAssignmentDto dto = new DoctorFacilityAssignmentDto();
            dto.setAssignmentId(a.getId());
            dto.setFacilityId(a.getFacility().getId());
            dto.setFacilityName(a.getFacility().getName());
            dto.setFacilityAddress(a.getFacility().getAddress());
            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorFacilityAssignments> updateAssignment(
            @PathVariable Long id,
            @RequestBody DoctorFacilityAssignments assignment) {
        Optional<DoctorFacilityAssignments> updatedAssignment = Optional
                .ofNullable(assignmentsService.updateAssignment(id, assignment));
        return updatedAssignment.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/facilities/{id}/doctors")
    public ResponseEntity<List<DoctorFacilityAssignmentDto>> getDoctorsByFacility(@PathVariable Long id) {
        List<DoctorFacilityAssignments> assignments = assignmentsService.getAssignmentsByFacility(id);
        List<DoctorFacilityAssignmentDto> dtos = assignments.stream().map(a -> {
            DoctorFacilityAssignmentDto dto = new DoctorFacilityAssignmentDto();
            dto.setAssignmentId(a.getId());
            dto.setFacilityId(a.getFacility().getId());
            dto.setFacilityName(a.getFacility().getName());
            dto.setFacilityAddress(a.getFacility().getAddress());
            dto.setRoomNumber(a.getRoomNumber());
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/facilities/{id}/assign-doctor")
    public ResponseEntity<DoctorFacilityAssignments> assignDoctorToFacility(
            @PathVariable Long id,
            @RequestBody DoctorFacilityAssignments assignment) {
        // Set the facility ID from the path
        assignment.setFacility(new Facilities());
        assignment.getFacility().setId(id);
        DoctorFacilityAssignments created = assignmentsService.createAssignment(assignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/facilities/{id}/doctors/{doctorId}")
public ResponseEntity<Void> removeDoctorFromFacility(
        @PathVariable Long id,
        @PathVariable Long doctorId) {
    List<DoctorFacilityAssignments> assignments = assignmentsService.getAssignments(doctorId, id);
    if (assignments.isEmpty()) {
        return ResponseEntity.notFound().build();
    }
    for (DoctorFacilityAssignments assignment : assignments) {
        var serviceLinks = doctorFacilityMedicalServiceRepository
            .findByDoctor_IdAndFacilityMedicalService_Facility_Id(doctorId, id);
        doctorFacilityMedicalServiceRepository.deleteAll(serviceLinks);
    }
    // Usuń przypisania lekarza do placówki
    assignments.forEach(a -> assignmentsService.deleteAssignment(a.getId()));
    return ResponseEntity.noContent().build();
}

    @GetMapping("/facilities/{id}/doctors/{doctorId}/schedule")
    public ResponseEntity<WeeklyScheduleDto> getDoctorWeeklySchedule(
            @PathVariable Long id,
            @PathVariable Long doctorId) {
        var assignments = assignmentsService.getAssignments(doctorId, id);
        if (assignments.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var assignment = assignments.get(0);
        WeeklyScheduleDto schedule = availabilitySlotsService.getWeeklyScheduleForAssignment(assignment.getId());
        return ResponseEntity.ok(schedule);
    }
    @PostMapping("/facilities/{id}/doctors/{doctorId}/schedule")
    public ResponseEntity<?> generateDoctorWeeklySchedule(
            @PathVariable Long id,
            @PathVariable Long doctorId,
            @RequestBody WeeklyScheduleDto schedule) {
        var assignments = assignmentsService.getAssignments(doctorId, id);
        if (assignments.isEmpty()) {
            return ResponseEntity.badRequest().body("Brak przypisania lekarza do placówki.");
        }
        schedule.setAssignmentId(assignments.get(0).getId());
        try {
            availabilitySlotsService.generateWeeklySlots(schedule);
            return ResponseEntity.status(HttpStatus.CREATED).body("Grafik wygenerowany.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
