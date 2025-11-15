package org.fixmed.fixmed.repository;

import java.util.List;
import java.util.Optional;

import org.fixmed.fixmed.model.Patients;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientsRepository extends JpaRepository<Patients, Long> {
    @Query("SELECT p FROM Patients p WHERE p.user.id = :userId")
    Optional<Patients> findByUser_Id(@Param("userId") Long userId);

    @Query("""
        SELECT p FROM Patients p
        JOIN p.user u
        WHERE LOWER(u.first_name) LIKE LOWER(CONCAT('%', :query, '%'))
           OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :query, '%'))
        ORDER BY u.last_name ASC
    """)
    List<Patients> searchByNameOrSurname(@Param("query") String query, Pageable pageable);

    boolean existsByPesel(String pesel);
}
