package com.askwinston.web.dto;

import com.askwinston.model.NotificationTarget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

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

