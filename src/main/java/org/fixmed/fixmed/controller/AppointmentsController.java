package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;

import org.fixmed.fixmed.auth.AuthenticationService;
import org.fixmed.fixmed.model.Appointments;
import org.fixmed.fixmed.model.dto.AppointmentHistoryDto;
import org.fixmed.fixmed.model.dto.AppointmentMedicalUpdateDto;
import org.fixmed.fixmed.service.AppointmentsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentsController {
    private final AppointmentsService appointmentsService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<Appointments> createAppointment(@RequestBody Appointments appointment) {
        return ResponseEntity.ok(appointmentsService.saveAppointment(appointment));
    }

    @GetMapping(params = { "doctorId", "date" })
    public ResponseEntity<List<Appointments>> getAppointmentsByDoctorAndDate(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(appointmentsService.getAppointmentsByDoctorAndDate(doctorId, date));
    }

    @GetMapping(params = { "doctorId" })
    public ResponseEntity<List<AppointmentHistoryDto>> getAppointmentsByDoctor(@RequestParam Long doctorId) {
        List<Appointments> appointments = appointmentsService.getAppointmentsByDoctor(doctorId);
        List<AppointmentHistoryDto> dtos = appointments.stream()
                .map(AppointmentHistoryDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Appointments> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam Appointments.AppointmentStatus status,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = authenticationService.getUserIdFromToken(authHeader.replace("Bearer ", ""));
        var appointmentOpt = appointmentsService.getAppointmentById(id);
        if (appointmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var appointment = appointmentOpt.get();
        if (status == Appointments.AppointmentStatus.CANCELED) {
            if (!appointment.getPatient().getUser().getId().equals(userId) &&
                    !appointment.getAssignment().getDoctor().getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            if (appointment.getStatus() != Appointments.AppointmentStatus.SCHEDULED) {
                return ResponseEntity.badRequest().body(null);
            }
            if (appointment.getDate().isBefore(LocalDate.now()) ||
                    (appointment.getDate().isEqual(LocalDate.now())
                            && appointment.getTime().isBefore(LocalTime.now()))) {
                return ResponseEntity.badRequest().body(null);
            }
        }
        return appointmentsService.updateAppointmentStatus(id, status)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkSlotAvailability(
            @RequestParam Long assignmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        boolean isAvailable = appointmentsService.isSlotAvailable(assignmentId, date, time);
        return ResponseEntity.ok(isAvailable);
    }

    @GetMapping("/history")
    public ResponseEntity<List<AppointmentHistoryDto>> getAppointmentHistory(
            @RequestParam Long patientId,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        List<AppointmentHistoryDto> history = appointmentsService.getAppointmentHistoryByPatientId(patientId, limit);
        return ResponseEntity.ok(history);
    }
    @GetMapping("/history/{id}")
    public ResponseEntity<List<AppointmentHistoryDto>> getAppointmentHistoryByUser(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        List<Appointments> appointmentsByUser = appointmentsService.getAppointmentsByUser(id);
        List<AppointmentHistoryDto> history = appointmentsByUser.stream().map(AppointmentHistoryDto::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentHistoryDto> getAppointmentDetails(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentsService.getAppointmentDetails(id));
    }

    @GetMapping("/upcoming")
    public ResponseEntity<List<AppointmentHistoryDto>> getUpcomingAppointments(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(defaultValue = "5") int limit) {

        List<AppointmentHistoryDto> upcoming;

        if (patientId != null) {
            upcoming = appointmentsService.getUpcomingAppointmentsByPatientId(patientId, limit);
        } else if (doctorId != null) {
            upcoming = appointmentsService.getUpcomingAppointmentsByDoctorId(doctorId, limit);
        } else {
            return ResponseEntity.badRequest().build(); // żadnego ID
        }

        return ResponseEntity.ok(upcoming);
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {
        LocalDate newDate = LocalDate.parse(body.get("newDate"));
        LocalTime newTime = LocalTime.parse(body.get("newTime"));
        Long userId = authenticationService.getUserIdFromToken(authHeader.replace("Bearer ", ""));
        var appointmentOpt = appointmentsService.getAppointmentById(id);
        if (appointmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var appointment = appointmentOpt.get();
        if (!appointment.getPatient().getUser().getId().equals(userId) &&
                !appointment.getAssignment().getDoctor().getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (appointment.getStatus() != Appointments.AppointmentStatus.SCHEDULED) {
            return ResponseEntity.badRequest().body("Można zmienić tylko zaplanowane wizyty.");
        }
        boolean isAvailable = appointmentsService.isSlotAvailable(
                appointment.getAssignment().getId(), newDate, newTime);
        if (!isAvailable) {
            return ResponseEntity.badRequest().body("Nowy termin jest niedostępny.");
        }
        if (newDate.isBefore(LocalDate.now()) ||
                (newDate.isEqual(LocalDate.now()) && newTime.isBefore(LocalTime.now()))) {
            return ResponseEntity.badRequest().body("Nie można ustawić wizyty w przeszłości.");
        }
        appointment.setDate(newDate);
        appointment.setTime(newTime);
        appointmentsService.saveAppointment(appointment);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/count-by-patient")
    public ResponseEntity<Long> countAppointmentsByPatient(@RequestParam Long patientId) {
        long count = appointmentsService.countAppointmentsByPatientId(patientId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/all-by-patient/{patientId}")
    public ResponseEntity<List<Map<String, Object>>> getAllAppointmentsByPatient(@PathVariable Long patientId) {
        List<Appointments> appointments = appointmentsService.getAllAppointmentsByPatientId(patientId);
        List<Map<String, Object>> result = appointments.stream()
                .map(appointment -> Map.<String, Object>of(
                        "appointmentId", appointment.getId(),
                        "doctorId", appointment.getAssignment().getDoctor().getId(),
                        "doctorName", appointment.getAssignment().getDoctor().getUser().getFirst_name() + " " +
                                appointment.getAssignment().getDoctor().getUser().getLast_name(),
                        "facility", appointment.getAssignment().getFacility().getName(),
                        "service", appointment.getService().getName(),
                        "date", appointment.getDate(),
                        "time", appointment.getTime(),
                        "status", appointment.getStatus()
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
    @PutMapping("/{id}/medical-info")
    public ResponseEntity<?> updateAppointmentMedicalInfo(
            @PathVariable Long id,
            @RequestBody AppointmentMedicalUpdateDto dto,
            @RequestHeader("Authorization") String authHeader) {

        Long userId = authenticationService.getUserIdFromToken(authHeader.replace("Bearer ", ""));

        return appointmentsService.updateMedicalInfo(id, dto, userId)
                .map(updated -> ResponseEntity.ok().build())
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
    }
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmAppointment(@PathVariable Long id, @RequestParam Long doctorId) {
        appointmentsService.confirmAppointment(id, doctorId);
        return ResponseEntity.ok().build();
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
