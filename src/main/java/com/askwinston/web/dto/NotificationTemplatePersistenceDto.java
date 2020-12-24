package com.askwinston.web.dto;

import com.askwinston.model.NotificationTarget;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class NotificationTemplatePersistenceDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private NotificationTarget target;
    private String notificationEventTypeName;
    private String subjectTemplate;
    @Type(type = "text")
    private String htmlTemplate;
    private boolean active;
    private boolean deferred;
    private Integer daysAfter;
    private boolean actual = true;
}
