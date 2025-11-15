package org.fixmed.fixmed.service.impl;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.*;
import org.fixmed.fixmed.model.dto.*;
import org.fixmed.fixmed.repository.*;
import org.fixmed.fixmed.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final PatientsRepository patientsRepository;
    private final DoctorsRepository doctorsRepository;

    @Override
    @Transactional
    public MessageDto sendMessage(SendMessageRequest request) {
        Patients patient = patientsRepository.findByUser_Id(request.getPatientUserId())
                .orElseThrow(() -> new IllegalArgumentException("Pacjent nie istnieje"));
        Doctors doctor = doctorsRepository.findByUser_Id(request.getDoctorUserId())
                .orElseThrow(() -> new IllegalArgumentException("Lekarz nie istnieje"));

        Message.SenderType senderType = Message.SenderType.valueOf(request.getSenderType());

        Message message = Message.builder()
                .patient(patient)
                .doctor(doctor)
                .senderType(senderType)
                .content(request.getContent())
                .sentAt(LocalDateTime.now())
                .isRead(false)
                .build();

        Message saved = messageRepository.save(message);

        return MessageDto.builder()
                .id(saved.getId())
                .patientId(saved.getPatient().getId())
                .doctorId(saved.getDoctor().getId())
                .senderType(saved.getSenderType().name())
                .content(saved.getContent())
                .sentAt(saved.getSentAt())
                .isRead(saved.isRead())
                .build();
    }

    @Override
    public List<MessageDto> getConversation(Long doctorId, Long patientId) {
        List<Message> messages = messageRepository.findByDoctor_IdAndPatient_IdOrderBySentAtAsc(doctorId, patientId);
        return messages.stream().map(m -> MessageDto.builder()
                .id(m.getId())
                .patientId(m.getPatient().getId())
                .doctorId(m.getDoctor().getId())
                .senderType(m.getSenderType().name())
                .content(m.getContent())
                .sentAt(m.getSentAt())
                .isRead(m.isRead())
                .build()).collect(Collectors.toList());
    }

    @Override
    public List<MessageDto> getConversationByUserIds(Long doctorUserId, Long patientUserId) {
        Doctors doctor = doctorsRepository.findByUser_Id(doctorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Lekarz nie istnieje"));
        Patients patient = patientsRepository.findByUser_Id(patientUserId)
                .orElseThrow(() -> new IllegalArgumentException("Pacjent nie istnieje"));
        List<Message> messages = messageRepository.findByDoctor_IdAndPatient_IdOrderBySentAtAsc(doctor.getId(),
                patient.getId());
        return messages.stream().map(m -> MessageDto.builder()
                .id(m.getId())
                .patientId(m.getPatient().getId())
                .doctorId(m.getDoctor().getId())
                .senderType(m.getSenderType().name())
                .content(m.getContent())
                .sentAt(m.getSentAt())
                .isRead(m.isRead())
                .build()).toList();
    }

    @Override
    public List<PatientConversationDto> getPatientsWithConversationByDoctorUserId(Long doctorUserId) {
        var doctor = doctorsRepository.findByUser_Id(doctorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Lekarz nie istnieje"));
        var messages = messageRepository.findAll().stream()
                .filter(m -> m.getDoctor().getId().equals(doctor.getId()))
                .collect(Collectors.toList());
        var patientIds = messages.stream()
                .map(m -> m.getPatient().getId())
                .distinct()
                .toList();
        var patients = patientsRepository.findAllById(patientIds);
        return patients.stream()
                .map(p -> PatientConversationDto.builder()
                        .patientId(p.getId())
                        .userId(p.getUser().getId())
                        .firstName(p.getUser().getFirst_name())
                        .lastName(p.getUser().getLast_name())
                        .build())
                .toList();
    }

    @Override
    public List<DoctorConversationDto> getDoctorsWithConversationByPatientUserId(Long patientUserId) {
        var patient = patientsRepository.findByUser_Id(patientUserId)
                .orElseThrow(() -> new IllegalArgumentException("Pacjent nie istnieje"));
        var messages = messageRepository.findAll().stream()
                .filter(m -> m.getPatient().getId().equals(patient.getId()))
                .collect(Collectors.toList());
        var doctorIds = messages.stream()
                .map(m -> m.getDoctor().getId())
                .distinct()
                .toList();
        var doctors = doctorsRepository.findAllById(doctorIds);
        return doctors.stream()
                .map(d -> DoctorConversationDto.builder()
                        .doctorId(d.getId())
                        .userId(d.getUser().getId())
                        .firstName(d.getUser().getFirst_name())
                        .lastName(d.getUser().getLast_name())
                        .build())
                .toList();
    }
}