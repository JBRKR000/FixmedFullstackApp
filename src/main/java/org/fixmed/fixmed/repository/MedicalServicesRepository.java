package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.MedicalServices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalServicesRepository extends JpaRepository<MedicalServices, Long> {
}
