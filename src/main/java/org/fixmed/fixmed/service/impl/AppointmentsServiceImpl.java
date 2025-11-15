package org.fixmed.fixmed.service.impl;

import org.fixmed.fixmed.model.Appointments;
import org.fixmed.fixmed.model.AvailabilitySlots;
import org.fixmed.fixmed.model.dto.AppointmentHistoryDto;
import org.fixmed.fixmed.model.dto.AppointmentMedicalUpdateDto;
import org.fixmed.fixmed.repository.AppointmentsRepository;
import org.fixmed.fixmed.repository.AvailabilitySlotsRepository;
import org.fixmed.fixmed.service.AppointmentRegisteredEvent;
import org.fixmed.fixmed.service.AppointmentsService;
import org.fixmed.fixmed.service.BlockedDaysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentsServiceImpl implements AppointmentsService {
    private final AppointmentsRepository appointmentsRepository;
    private final AvailabilitySlotsRepository availabilitySlotsRepository;
    private final BlockedDaysService blockedDaysService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public AppointmentsServiceImpl(AppointmentsRepository appointmentsRepository,
                                   AvailabilitySlotsRepository availabilitySlotsRepository,
                                   BlockedDaysService blockedDaysService, ApplicationEventPublisher eventPublisher) {
        this.appointmentsRepository = appointmentsRepository;
        this.availabilitySlotsRepository = availabilitySlotsRepository;
        this.blockedDaysService = blockedDaysService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Appointments saveAppointment(Appointments appointment) {
        boolean isAvailable = isSlotAvailable(
                appointment.getAssignment().getId(),
                appointment.getDate(),
                appointment.getTime());

        if (!isAvailable) {
            throw new RuntimeException("The selected slot is either unavailable or already booked.");
        }

        Appointments saved = appointmentsRepository.save(appointment);
        System.out.println(saved.toString());
        eventPublisher.publishEvent(new AppointmentRegisteredEvent(saved));
        return saved;
    }

    @Override
    public Optional<Appointments> getAppointmentById(Long id) {
        return appointmentsRepository.findById(id);
    }

    @Override
    public List<Appointments> getAppointmentsByDoctorAndDate(Long doctorId, LocalDate date) {
        return appointmentsRepository.findAppointmentsByAssignment_Doctor_IdAndDate(doctorId, date);
    }

    @Override
    public List<Appointments> getAppointmentsByDoctor(Long doctorId) {
        return appointmentsRepository.findAppointmentsByDoctorId(doctorId);
    }

    @Override
    public List<Appointments> getAppointmentsByUser(Long userId) {
        return appointmentsRepository.findAppointmentsByUserId(userId);
    }

    @Override
    public Optional<Appointments> updateAppointmentStatus(Long id, Appointments.AppointmentStatus status) {
        return appointmentsRepository.findById(id)
                .map(existingAppointment -> {
                    existingAppointment.setStatus(status);
                    return appointmentsRepository.save(existingAppointment);
                });
    }

    @Override
    public Page<Appointments> getAllAppointments(Pageable pageable) {
        return appointmentsRepository.findAll(pageable);
    }

    @Override
    public boolean isSlotAvailable(Long assignmentId, LocalDate date, LocalTime time) {
        if (blockedDaysService.isDayBlocked(assignmentId, date)) {
            return false;
        }
        AvailabilitySlots.DayOfWeek dayOfWeek = AvailabilitySlots.DayOfWeek.valueOf(date.getDayOfWeek().name());

        boolean slotExists = availabilitySlotsRepository
                .existsByAssignment_IdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        assignmentId,
                        dayOfWeek,
                        time);

        boolean slotOccupied = appointmentsRepository.existsByAssignment_IdAndDateAndTime(
                assignmentId,
                date,
                time);

        return slotExists && !slotOccupied;
    }

    @Override
    public List<LocalTime> getBookedTimesByFacilityAndDate(Long facilityId, LocalDate date) {
        return appointmentsRepository.findByFacilityIdAndDate(facilityId, date)
                .stream()
                .map(Appointments::getTime)
                .collect(Collectors.toList());
    }

    @Override
    public List<AppointmentHistoryDto> getAppointmentHistoryByPatientId(Long patientId, int limit) {
        return appointmentsRepository.findByPatient_IdAndStatusIn(
                        patientId,
                        List.of(Appointments.AppointmentStatus.CANCELED,
                                Appointments.AppointmentStatus.COMPLETED))
                .stream()
                .limit(limit > 0 ? limit : 10)
                .map(appointment -> AppointmentHistoryDto.builder()
                        .id(appointment.getId())
                        .serviceName(appointment.getService().getName())
                        .doctorName(appointment
                                .getAssignment().getDoctor().getUser().getFirst_name()
                                + " " +
                                appointment.getAssignment().getDoctor().getUser()
                                        .getLast_name())
                        .date(appointment.getDate())
                        .time(appointment.getTime())
                        .status(appointment.getStatus().name())
                        .facilityName(appointment.getAssignment().getFacility().getName())
                        .build())
                .toList();
    }

    @Override
    public AppointmentHistoryDto getAppointmentDetails(Long id) {
        Appointments appointment = appointmentsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        return AppointmentHistoryDto.fromEntity(appointment); // użycie pełnej konwersji
    }

    @Override
    public List<AppointmentHistoryDto> getUpcomingAppointmentsByPatientId(Long patientId, int limit) {
        LocalDate today = LocalDate.now();
        List<Appointments> upcoming = appointmentsRepository
                .findByPatient_IdAndDateGreaterThanEqualOrderByDateAscTimeAsc(patientId, today);
        return upcoming.stream()
                .filter(appointment ->
                        appointment.getStatus() == Appointments.AppointmentStatus.SCHEDULED ||
                                appointment.getStatus() == Appointments.AppointmentStatus.CONFIRMED)
                .limit(limit > 0 ? limit : Long.MAX_VALUE)
                .map(appointment -> AppointmentHistoryDto.builder()
                        .id(appointment.getId())
                        .serviceName(appointment.getService().getName())
                        .doctorName(appointment
                                .getAssignment().getDoctor().getUser().getFirst_name()
                                + " " +
                                appointment.getAssignment().getDoctor().getUser()
                                        .getLast_name())
                        .doctorId(appointment.getAssignment().getDoctor().getId())
                        .patientName(appointment.getPatient() != null
                                ? appointment.getPatient().getUser().getFirst_name()
                                + " " +
                                appointment.getPatient().getUser()
                                        .getLast_name()
                                : null)
                        .date(appointment.getDate())
                        .time(appointment.getTime())
                        .status(appointment.getStatus().name())
                        .facilityName(appointment.getAssignment().getFacility().getName())
                        .build())
                .toList();
    }

    @Override
    public List<AppointmentHistoryDto> getUpcomingAppointmentsByDoctorId(Long doctorId, int limit) {
        LocalDate today = LocalDate.now();
        List<Appointments> upcoming = appointmentsRepository
                .findByAssignment_Doctor_IdAndDateGreaterThanEqualOrderByDateAscTimeAsc(doctorId,
                        today);

        return upcoming.stream()
                .limit(limit > 0 ? limit : Long.MAX_VALUE)
                .map(appointment -> AppointmentHistoryDto.builder()
                        .id(appointment.getId())
                        .serviceName(appointment.getService().getName())
                        .doctorName(appointment
                                .getAssignment().getDoctor().getUser().getFirst_name()
                                + " " +
                                appointment.getAssignment().getDoctor().getUser()
                                        .getLast_name())
                        .doctorId(appointment.getAssignment().getDoctor().getId())
                        .patientName(appointment.getPatient() != null
                                ? appointment.getPatient().getUser().getFirst_name()
                                + " " +
                                appointment.getPatient().getUser()
                                        .getLast_name()
                                : null)
                        .date(appointment.getDate())
                        .time(appointment.getTime())
                        .status(appointment.getStatus().name())
                        .facilityName(appointment.getAssignment().getFacility().getName())
                        .build())
                .toList();
    }

    @Override
    public long countAppointmentsByPatientId(Long patientId) {
        return appointmentsRepository.countByPatient_Id(patientId);
    }

    @Override
    public List<Appointments> getAllAppointmentsByPatientId(Long patientId) {
        return appointmentsRepository.findByPatient_IdAndStatusIn(
                patientId,
                List.of(Appointments.AppointmentStatus.SCHEDULED, Appointments.AppointmentStatus.CANCELED, Appointments.AppointmentStatus.COMPLETED)
        );
    }

    @Override
    public Optional<Appointments> updateMedicalInfo(Long id, AppointmentMedicalUpdateDto dto, Long userId) {
        return appointmentsRepository.findById(id)
                .filter(appointment ->
                        appointment.getAssignment().getDoctor().getUser().getId().equals(userId))
                .map(appointment -> {
                    appointment.setDescription(dto.getDescription());
                    appointment.setDiagnosis(dto.getDiagnosis());
                    appointment.setRecommendations(dto.getRecommendations());
                    appointment.setPrescribedMedications(dto.getPrescribedMedications());
                    return appointmentsRepository.save(appointment);
                });
    }

    @Override
    public void confirmAppointment(Long appointmentId, Long doctorId) {
        Appointments appointment = appointmentsRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Wizyta nie istnieje"));

        if (!appointment.getAssignment().getDoctor().getId().equals(doctorId)) {
            throw new RuntimeException("Brak dostępu do potwierdzenia tej wizyty");
        }

        if (appointment.getStatus() != Appointments.AppointmentStatus.SCHEDULED) {
            throw new RuntimeException("Wizytę można potwierdzić tylko ze statusem SCHEDULED");
        }

        appointment.setStatus(Appointments.AppointmentStatus.CONFIRMED);
        appointmentsRepository.save(appointment);
    }

}