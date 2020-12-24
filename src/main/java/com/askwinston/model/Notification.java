package com.askwinston.model;

import lombok.Data;

import java.util.Date;

@Data
public class Notification {

    public enum Status {
        SENT,
        FAILED,
        PLANNED
    }

    private long id;
    private long targetEntityId;
    private Date date;
    private String email;

    private Status status;

    private NotificationTemplate template;
}
