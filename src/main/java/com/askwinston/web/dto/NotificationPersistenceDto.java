package com.askwinston.web.dto;

import com.askwinston.model.Notification;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
public class NotificationPersistenceDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date date;
    private String email;

    private Notification.Status status;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private NotificationTemplatePersistenceDto template;
}
