package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.Doctors;
import org.fixmed.fixmed.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorsRepository extends JpaRepository<Doctors, Long> {
    Optional<Doctors> findById(Long id);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Doctors d WHERE d.id = :id")
    boolean isDoctorAssignedToFacility(Long id);

    @Query(value = "SELECT COUNT(*) > 0 FROM doctors WHERE license_number = :licenseNumber", nativeQuery = true)
    boolean existsByLicenseNumber(@Param("licenseNumber") String licenseNumber);

    @Query("""
    SELECT d FROM Doctors d
    LEFT JOIN d.user u
    WHERE (:specialization IS NULL OR d.specialization = :specialization)
    AND (:city IS NULL OR d.city LIKE %:city%)
    AND (:name IS NULL OR (u.first_name LIKE %:name% OR u.last_name LIKE %:name%))
    AND (:gender IS NULL OR u.gender = :gender)
    """)
    Page<Doctors> searchDoctors(
        @Param("specialization") Doctors.Specialization specialization,
        @Param("city") String city,
        @Param("name") String name,
        @Param("gender") Users.Gender gender,
        Pageable pageable
    );

    @Query("SELECT d FROM Doctors d WHERE d.user.id = :userId")
    Optional<Doctors> findByUser_Id(@Param("userId") Long userId);

    @Query("""
        SELECT d FROM Doctors d
        WHERE d.id NOT IN (
            SELECT a.doctor.id FROM DoctorFacilityAssignments a
        )
    """)
    List<Doctors> findDoctorsWithoutFacility();

}
