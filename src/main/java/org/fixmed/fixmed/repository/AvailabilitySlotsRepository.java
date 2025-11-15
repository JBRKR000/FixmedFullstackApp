package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.AvailabilitySlots;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AvailabilitySlotsRepository extends JpaRepository<AvailabilitySlots, Long> {

        List<AvailabilitySlots> findByAssignment_Id(Long assignmentId);

        @Query("SELECT a FROM AvailabilitySlots a WHERE a.assignment.id = :assignmentId")
        Page<AvailabilitySlots> getAvailabilitySlotsByAssignment_Id(Long assignmentId, Pageable pageable);

        List<AvailabilitySlots> findByAssignment_IdIn(List<Long> assignmentIds);

        @Query("SELECT COUNT(a) > 0 FROM AvailabilitySlots a WHERE a.assignment.id = :assignmentId AND a.dayOfWeek = :dayOfWeek AND a.startTime <= :time AND a.endTime > :time")
        boolean existsByAssignment_IdAndDayOfWeekAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                        @Param("assignmentId") Long assignmentId,
                        @Param("dayOfWeek") AvailabilitySlots.DayOfWeek dayOfWeek,
                        @Param("time") LocalTime time);

        @Query("SELECT a FROM AvailabilitySlots a WHERE a.assignment.facility.id = :facilityId AND a.dayOfWeek = :dayOfWeek")
        List<AvailabilitySlots> findByFacilityIdAndDayOfWeek(@Param("facilityId") Long facilityId,
                        @Param("dayOfWeek") AvailabilitySlots.DayOfWeek dayOfWeek);

        @Query("SELECT a FROM AvailabilitySlots a WHERE a.assignment.doctor.id = :doctorId AND a.assignment.facility.id = :facilityId AND a.date = :date")
        List<AvailabilitySlots> findByDoctorIdAndFacilityIdAndDate(
                        @Param("doctorId") Long doctorId,
                        @Param("facilityId") Long facilityId,
                        @Param("date") LocalDate date);

        void deleteByAssignment_IdAndDateAndStartTimeGreaterThanEqualAndStartTimeLessThan(
                        Long assignmentId,
                        LocalDate date,
                        LocalTime from,
                        LocalTime to);

        List<AvailabilitySlots> findByAssignment_IdAndDateAndStartTimeGreaterThanEqualAndStartTimeLessThan(
                        Long assignmentId,
                        LocalDate date,
                        LocalTime from,
                        LocalTime to);

        void deleteByAssignment_Id(Long assignmentId);                

}
