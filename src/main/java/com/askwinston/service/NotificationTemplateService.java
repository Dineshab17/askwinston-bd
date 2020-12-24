package com.askwinston.service;

import com.askwinston.exception.NotFoundException;
import com.askwinston.model.NotificationTemplate;
import com.askwinston.notification.NotificationEventType;

import java.util.List;

public interface NotificationTemplateService {

    NotificationTemplate findById(long id) throws NotFoundException;

    List<NotificationTemplate> findAll();

    List<NotificationTemplate> findAllByNotificationEventTypeAndActive(NotificationEventType<?> type, boolean active);

    NotificationTemplate findByName(String name) throws NotFoundException;

    NotificationTemplate save(NotificationTemplate template);

    boolean existsById(Long id);

    NotificationTemplate deleteById(Long id) throws NotFoundException;

}
