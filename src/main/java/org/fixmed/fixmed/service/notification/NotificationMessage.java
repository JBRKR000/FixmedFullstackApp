package org.fixmed.fixmed.service.notification;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class NotificationMessage implements Serializable {
    private String recipient;
    private NotificationEmailType notyficationEmailType;
    private NotificationType type;
    private Map<String, String> metadata;
}