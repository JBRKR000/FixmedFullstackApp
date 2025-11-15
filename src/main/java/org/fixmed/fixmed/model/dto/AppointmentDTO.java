package org.fixmed.fixmed.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Long id;
    private LocalDate date;
    private LocalTime time;
    private String status;
    private String patientName;
    private String doctorName;
    private String doctorSpecialization;
    private String roomNumber;
    private String facilityName;
    private String facilityAddress;
    private String serviceName;
    private BigDecimal servicePrice;
}