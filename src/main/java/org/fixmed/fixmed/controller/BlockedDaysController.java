package org.fixmed.fixmed.controller;

import lombok.RequiredArgsConstructor;
import org.fixmed.fixmed.model.BlockedDays;
import org.fixmed.fixmed.service.BlockedDaysService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blocked-days")
@RequiredArgsConstructor
public class BlockedDaysController {
    private final BlockedDaysService blockedDaysService;

    @PostMapping
    public ResponseEntity<BlockedDays> createBlockedDay(@RequestBody BlockedDays blockedDay) {
        return ResponseEntity.ok(blockedDaysService.saveBlockedDay(blockedDay));
    }

    @GetMapping
    public ResponseEntity<List<BlockedDays>> getBlockedDays(@RequestParam Long assignmentId) {
        return ResponseEntity.ok(blockedDaysService.getBlockedDaysByAssignmentId(assignmentId));
    }
}