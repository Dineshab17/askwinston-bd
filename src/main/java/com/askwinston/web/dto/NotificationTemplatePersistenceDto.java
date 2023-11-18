package com.askwinston.web.dto;

import com.askwinston.model.NotificationTarget;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

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
    @Column(columnDefinition ="TEXT")
    private String htmlTemplate;
    private boolean active;
    private boolean deferred;
    private Integer daysAfter;
    private boolean actual = true;
}
