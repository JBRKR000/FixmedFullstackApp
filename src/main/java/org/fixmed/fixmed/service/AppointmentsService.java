package org.fixmed.fixmed.service;

import org.fixmed.fixmed.model.Appointments;
import org.fixmed.fixmed.model.dto.AppointmentHistoryDto;
import org.fixmed.fixmed.model.dto.AppointmentMedicalUpdateDto;
import org.fixmed.fixmed.model.dto.PatientSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public interface AppointmentsService {
    Appointments saveAppointment(Appointments appointment);

    boolean isSlotAvailable(Long assignmentId, LocalDate date, LocalTime time);

    Optional<Appointments> getAppointmentById(Long id);

    List<Appointments> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate date);

    List<Appointments> getAppointmentsByDoctor(Long doctorId);

    List<Appointments> getAppointmentsByUser(Long userId);

    Optional<Appointments> updateAppointmentStatus(Long id, Appointments.AppointmentStatus status);

    Page<Appointments> getAllAppointments(Pageable pageable);

    List<LocalTime> getBookedTimesByFacilityAndDate(Long facilityId, LocalDate date);

    List<AppointmentHistoryDto> getAppointmentHistoryByPatientId(Long patientId, int limit);

    AppointmentHistoryDto getAppointmentDetails(Long id);

    List<AppointmentHistoryDto> getUpcomingAppointmentsByPatientId(Long patientId, int limit);

    List<AppointmentHistoryDto> getUpcomingAppointmentsByDoctorId(Long doctorId, int limit);

    long countAppointmentsByPatientId(Long patientId);

    List<Appointments> getAllAppointmentsByPatientId(Long patientId);

    Optional<Appointments> updateMedicalInfo(Long id, AppointmentMedicalUpdateDto dto, Long userId);

    void confirmAppointment(Long appointmentId, Long doctorId);


}
