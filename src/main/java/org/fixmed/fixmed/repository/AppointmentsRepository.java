package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.Appointments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentsRepository extends JpaRepository<Appointments, Long> {
    List<Appointments> findAppointmentsByAssignment_Doctor_IdAndDate(Long doctorId, LocalDate date);
    @Query("""
    SELECT a FROM Appointments a
    JOIN FETCH a.assignment ass
    JOIN FETCH ass.doctor d
    JOIN FETCH d.user
    JOIN FETCH ass.facility
    JOIN FETCH a.patient p
    JOIN FETCH p.user
    WHERE a.id = :id
""")
    Optional<Appointments> findByIdWithAllRelations(@Param("id") Long id);

    @Query("""
                SELECT a
                FROM Appointments a
                JOIN a.assignment dfa
                JOIN dfa.doctor d
                WHERE d.id = :doctorId
            """)
    List<Appointments> findAppointmentsByDoctorId(@Param("doctorId") Long doctorId);

    @Query("""
                SELECT a
                FROM Appointments a
                JOIN a.patient p
                JOIN p.user u
                WHERE u.id = :userId
            """)
    List<Appointments> findAppointmentsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) > 0 FROM Appointments a WHERE a.assignment.id = :assignmentId AND a.date = :date AND a.time = :time")
    boolean existsByAssignment_IdAndDateAndTime(
            @Param("assignmentId") Long assignmentId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );

    @Query("SELECT a FROM Appointments a WHERE a.assignment.facility.id = :facilityId AND a.date = :date")
    List<Appointments> findByFacilityIdAndDate(@Param("facilityId") Long facilityId, @Param("date") LocalDate date);


    List<Appointments> findByPatient_IdAndStatusIn(Long patientId, List<Appointments.AppointmentStatus> statuses);

    List<Appointments> findByPatient_IdAndDateGreaterThanEqualOrderByDateAscTimeAsc(Long patientId, LocalDate date);

    List<Appointments> findByAssignment_Doctor_IdAndDateGreaterThanEqualOrderByDateAscTimeAsc(Long doctorId, LocalDate date);

    long countByPatient_Id(Long patientId);
}
