package org.fixmed.fixmed.service.notification;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fixmed.fixmed.model.Appointments;
import org.fixmed.fixmed.model.DoctorFacilityAssignments;
import org.fixmed.fixmed.model.Doctors;
import org.fixmed.fixmed.model.dto.DoctorDetailsDto;
import org.fixmed.fixmed.repository.AppointmentsRepository;
import org.fixmed.fixmed.service.AppointmentRegisteredEvent;
import org.fixmed.fixmed.service.AppointmentsService;
import org.fixmed.fixmed.service.DoctorFacilityAssignmentsService;
import org.fixmed.fixmed.service.DoctorsService;
import org.fixmed.fixmed.service.PatientsService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AppointmentNotificationHandler {

    private final RabbitTemplate rabbitTemplate;

    private final AppointmentsRepository appointmentsService;
    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingKey}")
    private String routingKey;

    @PostConstruct
    public void logRabbitConfig() {
        log.info("🔁 RabbitMQ config: exchange = {}, routingKey = {}", exchange, routingKey);
    }

    @EventListener
    public void handleAppointmentRegistered(AppointmentRegisteredEvent event) {
        Long appointmentId = event.getAppointment().getId();

        Optional<Appointments> optional = appointmentsService.findByIdWithAllRelations(appointmentId);
        if (optional.isEmpty()) {
            log.warn("Brak danych wizyty — wiadomość nie została wysłana");
            return;
        }

        Appointments appointment = optional.get();

        String doctorName = appointment.getAssignment()
                .getDoctor()
                .getUser()
                .getFirst_name() + " " + appointment.getAssignment().getDoctor().getUser().getLast_name();

        String facilityName = appointment.getAssignment()
                .getFacility()
                .getName();

        String patientEmail = appointment.getPatient()
                .getUser()
                .getEmail();

        Map<String, String> metadata = Map.of(
                "doctorName", doctorName,
                "facilityName", facilityName,
                "date", appointment.getDate().toString(),
                "time", appointment.getTime().toString()
        );

        NotificationMessage message = new NotificationMessage();
        message.setNotyficationEmailType(NotificationEmailType.VISIT_REGISTERED);
        message.setType(NotificationType.EMAIL);
//        message.setRecipient(patientEmail);
        message.setRecipient("jkowalski1331@gmail.com");
        message.setMetadata(metadata);

        log.info("📤 Sending message to exchange={}, routingKey={}", exchange, routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        log.info("✅ Notification sent to queue: {}", message);
    }
}
