package org.fixmed.fixmed.model.dto;

import lombok.Builder;
import lombok.Data;
import org.fixmed.fixmed.model.Appointments;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class AppointmentHistoryDto {
    private Long id;
    private String serviceName;
    private String doctorName;
    private Long doctorId;
    private LocalDate date;
    private LocalTime time;
    private String status;
    private String facilityName;
    private String patientName;
    private Long patientId;
    private String description;
    private String diagnosis;
    private String recommendations;
    private String prescribedMedications;
    private String mockResultFileUrl;
    private List<AttachmentDto> attachments;

    @Data
    @Builder
    public static class AttachmentDto {
        private Long id;
        private String name;
        private String url;
    }

    public static AppointmentHistoryDto fromEntity(Appointments appointment) {
        AppointmentHistoryDtoBuilder builder = AppointmentHistoryDto.builder()
                .id(appointment.getId())
                .serviceName(appointment.getService().getName())
                .doctorName(
                        appointment.getAssignment().getDoctor().getUser().getFirst_name() + " " +
                                appointment.getAssignment().getDoctor().getUser().getLast_name()
                )
                .doctorId(appointment.getAssignment().getDoctor().getId())
                .date(appointment.getDate())
                .time(appointment.getTime())
                .status(appointment.getStatus() != null ? appointment.getStatus().name() : null)
                .facilityName(appointment.getAssignment().getFacility().getName())
                .patientName(
                        appointment.getPatient().getUser().getFirst_name() + " " +
                                appointment.getPatient().getUser().getLast_name()
                )
                .patientId(appointment.getPatient().getId())
                .description(appointment.getDescription())
                .diagnosis(appointment.getDiagnosis())
                .recommendations(appointment.getRecommendations())
                .prescribedMedications(appointment.getPrescribedMedications())
                .mockResultFileUrl("https://example.com/test-results.pdf");
        // MOCKOWE ZAŁĄCZNIKI – tylko jeśli zakończona wizyta
        if (appointment.getStatus() == Appointments.AppointmentStatus.COMPLETED) {
            builder.attachments(List.of(
                    AttachmentDto.builder()
                            .id(1L)
                            .name("USG prostaty.pdf")
                            .url("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
                            .build(),
                    AttachmentDto.builder()
                            .id(2L)
                            .name("Badania krwi.pdf")
                            .url("https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
                            .build()
            ));
        }

        return builder.build();
    }
}