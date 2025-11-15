package org.fixmed.fixmed.service.impl;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.AvailabilitySlots;
import org.fixmed.fixmed.model.DoctorFacilityAssignments;
import org.fixmed.fixmed.model.dto.AvailabilitySlotDto;
import org.fixmed.fixmed.model.dto.WeeklyScheduleDto;
import org.fixmed.fixmed.repository.AvailabilitySlotsRepository;
import org.fixmed.fixmed.repository.DoctorFacilityAssignmentsRepository;
import org.fixmed.fixmed.service.AvailabilitySlotsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvailabilitySlotsServiceImpl implements AvailabilitySlotsService {

    private final AvailabilitySlotsRepository availabilitySlotsRepository;
    private final DoctorFacilityAssignmentsRepository doctorFacilityAssignmentsRepository;
    private final AppointmentsServiceImpl appointmentsService;

    @Override
    public Optional<AvailabilitySlots> getAvailabilitySlotbyId(Long id) {
        return availabilitySlotsRepository.findById(id);
    }

    @Override
    public AvailabilitySlots saveAvailabilitySlot(AvailabilitySlots availabilitySlots) {
        return availabilitySlotsRepository.save(availabilitySlots);
    }

    @Override
    public Page<AvailabilitySlots> getAllAvailabilitySlots(Pageable pageable) {
        return availabilitySlotsRepository.findAll(pageable);
    }

    @Transactional
    @Override
    public AvailabilitySlots updateAvailabilitySlot(Long id, AvailabilitySlots availabilitySlotsDetails) {
        return availabilitySlotsRepository.findById(id).map(slot -> {
            slot.setStartTime(availabilitySlotsDetails.getStartTime());
            slot.setEndTime(availabilitySlotsDetails.getEndTime());
            slot.setDayOfWeek(availabilitySlotsDetails.getDayOfWeek());
            return availabilitySlotsRepository.save(slot);
        }).orElseThrow(() -> new RuntimeException("Slot not found with id: " + id));
    }

    @Override
    public Page<AvailabilitySlots> getAvailabilitySlotsByAssignmentsId(Long doctorId, Pageable pageable) {
        return availabilitySlotsRepository.getAvailabilitySlotsByAssignment_Id(doctorId, pageable);
    }

    @Override
    public List<AvailabilitySlots> getSlotsByDoctorId(Long doctorId) {
        List<Long> assignmentIds = doctorFacilityAssignmentsRepository
                .findByDoctor_Id(doctorId)
                .stream()
                .map(DoctorFacilityAssignments::getId)
                .toList();

        return availabilitySlotsRepository.findByAssignment_IdIn(assignmentIds);
    }

    @Override
    public void deleteAvailabilitySlots(Long id) {
        availabilitySlotsRepository.deleteById(id);
    }

    @Override
    public List<AvailabilitySlots> getSlotsByFacilityIdAndDate(Long facilityId, LocalDate date) {
        return availabilitySlotsRepository.findByFacilityIdAndDayOfWeek(facilityId,
                AvailabilitySlots.DayOfWeek.valueOf(date.getDayOfWeek().name()));
    }

    public List<AvailabilitySlots> getSlotsByDoctorAndFacilityAndDate(Long doctorId, Long facilityId, LocalDate date) {
        return availabilitySlotsRepository.findByDoctorIdAndFacilityIdAndDate(doctorId, facilityId, date);
    }

    public static AvailabilitySlotDto mapToDto(AvailabilitySlots slot) {
    return AvailabilitySlotDto.builder()
            .id(slot.getId())
            .assignmentId(slot.getAssignment().getId())
            .doctorName(slot.getAssignment().getDoctor().getUser().getFirst_name() + " " +
                        slot.getAssignment().getDoctor().getUser().getLast_name())
            .date(slot.getDate())
            .startTime(slot.getStartTime())
            .endTime(slot.getEndTime())
            .facilityName(slot.getAssignment().getFacility().getName())
            .build();
}

    // -----------------------------------------------------------------------------------------
    // This method generates availability slots automatically based on the provided
    // base slot.
    // It checks for collisions with existing appointments and creates new slots
    // accordingly.
    // The method assumes that the base slot has already been validated and exists
    // in the database.
    // -----------------------------------------------------------------------------------------
    @Transactional
    @Override
    public void generateSlotsAutomatically(AvailabilitySlots baseSlot) {
        if (baseSlot.getSlotLengthMinutes() <= 0) {
            throw new IllegalArgumentException("Długość slotu musi być większa niż 0 minut");
        }

        DoctorFacilityAssignments assignment = doctorFacilityAssignmentsRepository
                .findById(baseSlot.getAssignment().getId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono przypisania lekarza do placówki"));

        baseSlot.setAssignment(assignment);

        availabilitySlotsRepository.deleteByAssignment_IdAndDateAndStartTimeGreaterThanEqualAndStartTimeLessThan(
                baseSlot.getAssignment().getId(),
                baseSlot.getDate(),
                baseSlot.getStartTime(),
                baseSlot.getEndTime());

        final LocalTime[] current = new LocalTime[] { baseSlot.getStartTime() };
        LocalTime end = baseSlot.getEndTime();
        int length = baseSlot.getSlotLengthMinutes();

        while (!current[0].plusMinutes(length).isAfter(end)) {
            LocalTime slotEnd = current[0].plusMinutes(length);

            boolean hasCollision = appointmentsService.getAppointmentsByDoctorAndDate(
                    baseSlot.getAssignment().getDoctor().getId(),
                    baseSlot.getDate()).stream().anyMatch(appt -> {
                        LocalTime apptTime = appt.getTime();
                        return !apptTime.isBefore(current[0]) && apptTime.isBefore(slotEnd);
                    });

            if (!hasCollision) {
                AvailabilitySlots newSlot = new AvailabilitySlots();
                newSlot.setAssignment(baseSlot.getAssignment());
                newSlot.setDate(baseSlot.getDate());
                newSlot.setStartTime(current[0]);
                newSlot.setEndTime(slotEnd);
                newSlot.setSlotLengthMinutes(length);
                newSlot.setDayOfWeek(baseSlot.getDayOfWeek());
                availabilitySlotsRepository.save(newSlot);
            }

            current[0] = slotEnd;
        }
    }

    @Transactional
    @Override
    public AvailabilitySlotDto updateSlotLength(Long id, int newLength) {
        AvailabilitySlots slot = availabilitySlotsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Slot not found"));

        LocalTime newEndTime = slot.getStartTime().plusMinutes(newLength);

        // Sprawdź czy w nowym zakresie czasu są jakieś wizyty
        boolean hasConflict = appointmentsService.getAppointmentsByDoctorAndDate(
                slot.getAssignment().getDoctor().getId(),
                slot.getDate()).stream().anyMatch(appt -> {
                    LocalTime apptTime = appt.getTime();
                    return !apptTime.isBefore(slot.getStartTime()) && apptTime.isBefore(newEndTime);
                });

        if (hasConflict) {
            throw new IllegalStateException("Zmiana koliduje z istniejącą wizytą");
        }

        List<LocalTime> timesToDelete = availabilitySlotsRepository
                .findByAssignment_IdAndDateAndStartTimeGreaterThanEqualAndStartTimeLessThan(
                        slot.getAssignment().getId(),
                        slot.getDate(),
                        slot.getStartTime().plusMinutes(1),
                        newEndTime)
                .stream()
                .map(AvailabilitySlots::getStartTime)
                .collect(Collectors.toList());

        boolean anyOccupied = appointmentsService.getAppointmentsByDoctorAndDate(
                slot.getAssignment().getDoctor().getId(),
                slot.getDate()).stream().anyMatch(appt -> timesToDelete.contains(appt.getTime()));

        if (anyOccupied) {
            throw new IllegalStateException("Nie można usunąć slotów, bo któryś jest już zajęty wizytą");
        }

        availabilitySlotsRepository.deleteByAssignment_IdAndDateAndStartTimeGreaterThanEqualAndStartTimeLessThan(
                slot.getAssignment().getId(),
                slot.getDate(),
                slot.getStartTime().plusMinutes(1),
                newEndTime);

        slot.setEndTime(newEndTime);
        slot.setSlotLengthMinutes(newLength);
        availabilitySlotsRepository.save(slot);

        return mapToDto(slot);
    }

    @Transactional
    @Override
    public void generateWeeklySlots(WeeklyScheduleDto schedule) {
        DoctorFacilityAssignments assignment = doctorFacilityAssignmentsRepository.findById(schedule.getAssignmentId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono przypisania lekarza do placówki"));
        for (LocalDate date = schedule.getFromDate(); !date.isAfter(schedule.getToDate()); date = date.plusDays(1)) {
            final LocalDate currentDate = date;
            String dayName = currentDate.getDayOfWeek().name();
            schedule.getDays().stream()
                    .filter(d -> d.getDayOfWeek().equalsIgnoreCase(dayName))
                    .forEach(day -> {
                        AvailabilitySlots baseSlot = new AvailabilitySlots();
                        baseSlot.setAssignment(assignment);
                        baseSlot.setDate(currentDate);
                        baseSlot.setDayOfWeek(AvailabilitySlots.DayOfWeek.valueOf(dayName));
                        baseSlot.setStartTime(day.getStartTime());
                        baseSlot.setEndTime(day.getEndTime());
                        baseSlot.setSlotLengthMinutes(day.getSlotLengthMinutes());
                        this.generateSlotsAutomatically(baseSlot);
                    });
        }
    }

    @Override
    public WeeklyScheduleDto getWeeklyScheduleForAssignment(Long assignmentId) {
    List<AvailabilitySlots> slots = availabilitySlotsRepository.findByAssignment_Id(assignmentId);
    WeeklyScheduleDto dto = new WeeklyScheduleDto();
    dto.setAssignmentId(assignmentId);
    if (slots.isEmpty()) {
        dto.setDays(List.of());
        return dto;
    }
    LocalDate minDate = slots.stream().map(AvailabilitySlots::getDate).min(LocalDate::compareTo).orElse(null);
    LocalDate maxDate = slots.stream().map(AvailabilitySlots::getDate).max(LocalDate::compareTo).orElse(null);
    dto.setFromDate(minDate);
    dto.setToDate(maxDate);
    var days = slots.stream()
        .collect(Collectors.groupingBy(
            s -> s.getDayOfWeek().name() + s.getStartTime() + s.getEndTime() + s.getSlotLengthMinutes()
        ))
        .values().stream()
        .map(list -> {
            AvailabilitySlots s = list.get(0);
            WeeklyScheduleDto.DaySchedule day = new WeeklyScheduleDto.DaySchedule();
            day.setDayOfWeek(s.getDayOfWeek().name());
            day.setStartTime(s.getStartTime());
            day.setEndTime(s.getEndTime());
            day.setSlotLengthMinutes(s.getSlotLengthMinutes());
            return day;
        })
        .collect(Collectors.toList());

    dto.setDays(days);
    return dto;
}

}
