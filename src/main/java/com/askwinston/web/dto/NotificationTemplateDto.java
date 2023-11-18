package com.askwinston.web.dto;

import com.askwinston.model.NotificationTarget;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplateDto {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private NotificationTarget target;

    @NotNull
    private NotificationEventTypeDto notificationEventType;

    private String subjectTemplate;

    private String htmlTemplate;

    @NotNull
    private boolean active;

    @NotNull
    private boolean deferred;

    private Integer daysAfter;
}

