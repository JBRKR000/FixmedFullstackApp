package org.fixmed.fixmed.service;

import org.fixmed.fixmed.model.BlockedDays;

import java.time.LocalDate;
import java.util.List;

public interface BlockedDaysService {
    BlockedDays saveBlockedDay(BlockedDays blockedDay);
    List<BlockedDays> getBlockedDaysByAssignmentId(Long assignmentId);
    boolean isDayBlocked(Long assignmentId, LocalDate date);
}