package org.fixmed.fixmed.service;


import org.fixmed.fixmed.model.dto.DoctorConversationDto;
import org.fixmed.fixmed.model.dto.MessageDto;
import org.fixmed.fixmed.model.dto.PatientConversationDto;
import org.fixmed.fixmed.model.dto.SendMessageRequest;

import java.util.List;

public interface MessageService {
    MessageDto sendMessage(SendMessageRequest request);
    List<MessageDto> getConversation(Long doctorId, Long patientId);
    List<MessageDto> getConversationByUserIds(Long doctorUserId, Long patientUserId);
    List<PatientConversationDto> getPatientsWithConversationByDoctorUserId(Long doctorUserId);
    List<DoctorConversationDto> getDoctorsWithConversationByPatientUserId(Long patientUserId);
}