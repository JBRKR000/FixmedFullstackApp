package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.dto.*;
import org.fixmed.fixmed.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<MessageDto> sendMessage(@RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(messageService.sendMessage(request));
    }

    @GetMapping("/conversation")
    public ResponseEntity<List<MessageDto>> getConversation(
            @RequestParam Long doctorUserId,
            @RequestParam Long patientUserId) {
        return ResponseEntity.ok(messageService.getConversationByUserIds(doctorUserId, patientUserId));
    }
    @GetMapping("/patients-for-doctor")
    public ResponseEntity<List<PatientConversationDto>> getPatientsForDoctor(@RequestParam Long doctorUserId) {
        List<PatientConversationDto> patients = messageService.getPatientsWithConversationByDoctorUserId(doctorUserId);
        return ResponseEntity.ok(patients);
    }
    @GetMapping("/doctors-for-patient")
    public ResponseEntity<List<DoctorConversationDto>> getDoctorsForPatient(@RequestParam Long patientUserId) {
        List<DoctorConversationDto> doctors = messageService.getDoctorsWithConversationByPatientUserId(patientUserId);
        return ResponseEntity.ok(doctors);
    }
}