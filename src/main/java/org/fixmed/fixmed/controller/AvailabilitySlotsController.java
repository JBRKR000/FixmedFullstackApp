package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;

import org.fixmed.fixmed.auth.AuthenticationService;
import org.fixmed.fixmed.model.AvailabilitySlots;
import org.fixmed.fixmed.model.dto.AvailabilitySlotDto;
import org.fixmed.fixmed.model.dto.WeeklyScheduleDto;
import org.fixmed.fixmed.repository.AvailabilitySlotsRepository;
import org.fixmed.fixmed.service.AppointmentsService;
import org.fixmed.fixmed.service.AvailabilitySlotsService;
import org.fixmed.fixmed.service.impl.AvailabilitySlotsServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/slots")
@RequiredArgsConstructor
public class AvailabilitySlotsController {
    private final AvailabilitySlotsService availabilitySlotsService;
    private final AuthenticationService authenticationService;
    private final AppointmentsService appointmentsService;
    private final AvailabilitySlotsRepository availabilitySlotsRepository;

    @GetMapping(params = "doctorId")
    public ResponseEntity<List<AvailabilitySlots>> getSlotsByDoctorId(@RequestParam Long doctorId) {
        List<AvailabilitySlots> slots = availabilitySlotsService.getSlotsByDoctorId(doctorId);
        return ResponseEntity.ok(slots);
    }

    @PostMapping
    public ResponseEntity<?> saveAvailabilitySlot(@RequestBody AvailabilitySlots availabilitySlot) {
        try {
            availabilitySlotsService.generateSlotsAutomatically(availabilitySlot);
            return ResponseEntity.status(HttpStatus.CREATED).body("Sloty zostały wygenerowane poprawnie.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<AvailabilitySlots>> getSlotsByAssignmentId(@RequestParam Long assignmentId,
            Pageable pageable) {
        Page<AvailabilitySlots> slots = availabilitySlotsService.getAvailabilitySlotsByAssignmentsId(assignmentId,
                pageable);
        return ResponseEntity.ok(slots);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvailabilitySlots> updateAvailabilitySlot(
            @PathVariable Long id,
            @RequestBody AvailabilitySlots availabilitySlot,
            @RequestHeader("Authorization") String token) {

        String userRole = authenticationService.getRoleFromToken(token);
        if (!"LEKARZ".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
        AvailabilitySlots updatedSlot = availabilitySlotsService.updateAvailabilitySlot(id, availabilitySlot);
        return ResponseEntity.ok(updatedSlot);
    }

    @GetMapping("/available")
    public ResponseEntity<List<LocalTime>> getAvailableAppointments(
            @RequestParam Long facilityId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AvailabilitySlots> slots = availabilitySlotsService.getSlotsByFacilityIdAndDate(facilityId, date);
        List<LocalTime> bookedTimes = appointmentsService.getBookedTimesByFacilityAndDate(facilityId, date);
        List<LocalTime> availableTimes = slots.stream()
                .filter(slot -> slot.getSlotLengthMinutes() > 0)
                .flatMap(slot -> {
                    int length = slot.getSlotLengthMinutes();
                    return java.util.stream.LongStream
                            .iterate(0, n -> n + length)
                            .limit((slot.getStartTime().until(slot.getEndTime(), ChronoUnit.MINUTES)) / length)
                            .mapToObj(minutes -> slot.getStartTime().plusMinutes(minutes));
                })
                .filter(time -> !bookedTimes.contains(time))
                .collect(Collectors.toList());

        return ResponseEntity.ok(availableTimes);
    }

    @GetMapping("/availability")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam Long facilityId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AvailabilitySlots> slots = availabilitySlotsService.getSlotsByDoctorAndFacilityAndDate(doctorId,
                facilityId, date);
        List<LocalTime> bookedTimes = appointmentsService.getBookedTimesByFacilityAndDate(facilityId, date);
        List<LocalTime> availableTimes = slots.stream()
                .flatMap(slot -> {
                    int length = slot.getSlotLengthMinutes();
                    return java.util.stream.LongStream
                            .iterate(0, n -> n + length)
                            .limit((slot.getStartTime().until(slot.getEndTime(), ChronoUnit.MINUTES)) / length)
                            .mapToObj(minutes -> slot.getStartTime().plusMinutes(minutes));
                })
                .filter(time -> !bookedTimes.contains(time))
                .collect(Collectors.toList());

        return ResponseEntity.ok(availableTimes);
    }

    @GetMapping("/dto")
    public ResponseEntity<List<AvailabilitySlotDto>> getSlotsDtoByDoctorId(@RequestParam Long doctorId) {
        List<AvailabilitySlots> slots = availabilitySlotsService.getSlotsByDoctorId(doctorId);
        List<AvailabilitySlotDto> dtos = slots.stream()
                .map(AvailabilitySlotsServiceImpl::mapToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/length")
    public ResponseEntity<?> updateSlotLength(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        int newLength = body.get("slotLengthMinutes");
        AvailabilitySlots slot = availabilitySlotsService.getAvailabilitySlotbyId(id)
                .orElse(null);
        if (slot == null) {
            return ResponseEntity.notFound().build();
        }
        boolean isAvailable = appointmentsService.isSlotAvailable(
                slot.getAssignment().getId(),
                slot.getDate(),
                slot.getStartTime());
        if (!isAvailable) {
            return ResponseEntity.badRequest().body("Slot nie jest wolny – nie można zmienić długości.");
        }

        try {
            AvailabilitySlotDto dto = availabilitySlotsService.updateSlotLength(id, newLength);
            return ResponseEntity.ok(dto);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/bulk/weekly")
    public ResponseEntity<?> generateWeeklySlots(@RequestBody WeeklyScheduleDto schedule) {
        try {
            availabilitySlotsService.generateWeeklySlots(schedule);
            return ResponseEntity.status(HttpStatus.CREATED).body("Sloty wygenerowane zgodnie z grafikiem.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
