package org.fixmed.fixmed.model.dto;

import lombok.Data;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;

@Data
public class WeeklyScheduleDto {
    private Long assignmentId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private List<DaySchedule> days;

    @Data
    public static class DaySchedule {
        private String dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private int slotLengthMinutes;
    }
}