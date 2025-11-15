package org.fixmed.fixmed.service;
import org.fixmed.fixmed.model.AvailabilitySlots;
import org.fixmed.fixmed.model.dto.AvailabilitySlotDto;
import org.fixmed.fixmed.model.dto.WeeklyScheduleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.time.LocalDate;
import java.util.List;
@Service
public interface AvailabilitySlotsService {
    Optional<AvailabilitySlots> getAvailabilitySlotbyId (Long id);
    AvailabilitySlots saveAvailabilitySlot(AvailabilitySlots availabilitySlot);
    Page<AvailabilitySlots> getAllAvailabilitySlots(Pageable pageable);
    AvailabilitySlots updateAvailabilitySlot(Long id, AvailabilitySlots availabilitySlotsDetails);
    Page<AvailabilitySlots> getAvailabilitySlotsByAssignmentsId(Long doctorId, Pageable pageable);
    List<AvailabilitySlots> getSlotsByDoctorId(Long doctorId);
    void deleteAvailabilitySlots(Long id);
    List<AvailabilitySlots> getSlotsByFacilityIdAndDate(Long facilityId, LocalDate date);
    List<AvailabilitySlots> getSlotsByDoctorAndFacilityAndDate(Long doctorId, Long facilityId, LocalDate date);
    void generateSlotsAutomatically(AvailabilitySlots baseSlot);
    AvailabilitySlotDto updateSlotLength(Long id, int newLength);
    void generateWeeklySlots(WeeklyScheduleDto schedule);
    WeeklyScheduleDto getWeeklyScheduleForAssignment(Long assignmentId);
}
