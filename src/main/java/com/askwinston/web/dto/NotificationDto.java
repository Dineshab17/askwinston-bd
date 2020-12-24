package com.askwinston.web.dto;

import com.askwinston.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {

    private long id;
    private Date date;
    private String email;

    private Notification.Status status;

    private String notificationTemplate;
}
