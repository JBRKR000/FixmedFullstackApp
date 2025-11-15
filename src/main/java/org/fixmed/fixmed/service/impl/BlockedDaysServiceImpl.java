package org.fixmed.fixmed.service.impl;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.BlockedDays;
import org.fixmed.fixmed.repository.BlockedDaysRepository;
import org.fixmed.fixmed.service.BlockedDaysService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlockedDaysServiceImpl implements BlockedDaysService {
    private final BlockedDaysRepository blockedDaysRepository;

    @Override
    public BlockedDays saveBlockedDay(BlockedDays blockedDay) {
        return blockedDaysRepository.save(blockedDay);
    }

    @Override
    public List<BlockedDays> getBlockedDaysByAssignmentId(Long assignmentId) {
        return blockedDaysRepository.findByAssignment_Id(assignmentId);
    }

    @Override
    public boolean isDayBlocked(Long assignmentId, LocalDate date) {
        return blockedDaysRepository.existsByAssignment_IdAndDate(assignmentId, date);
    }
}