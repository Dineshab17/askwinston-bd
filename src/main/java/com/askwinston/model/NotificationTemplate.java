package com.askwinston.model;

import com.askwinston.notification.NotificationEventType;
import lombok.Data;

@Data
public class NotificationTemplate {

    private Long id;

    private String name;

    private NotificationTarget target;

    private NotificationEventType<?> notificationEventType;

    private String subjectTemplate;
    private String htmlTemplate;
    private boolean active;
    private boolean deferred;
    private int daysAfter;
    private boolean actual = true;
}
