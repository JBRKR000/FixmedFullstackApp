package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.List;
import java.util.Optional;

public interface ReceptionistRepository extends JpaRepository<Receptionist, Long> {
    List<Receptionist> findByFacilityId(Long facilityId);
    Optional<Receptionist> findByUserId(Long userId);
}