package org.fixmed.fixmed.repository;

import org.fixmed.fixmed.model.BlockedDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BlockedDaysRepository extends JpaRepository<BlockedDays, Long> {
    List<BlockedDays> findByAssignment_Id(Long assignmentId);
    boolean existsByAssignment_IdAndDate(Long assignmentId, LocalDate date);
}