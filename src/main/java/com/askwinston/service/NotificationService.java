package com.askwinston.service;

import com.askwinston.exception.NotFoundException;
import com.askwinston.model.Notification;

import java.util.Date;
import java.util.List;

public interface NotificationService {

    Notification findById(long id) throws NotFoundException;

    List<Notification> findAll();

    List<Notification> findAllByStatusAndDateBefore(Notification.Status status, Date date);

    Notification save(Notification notification);

    void saveAll(List<Notification> notifications);

    boolean existsById(Long id);

    Notification deleteById(Long id) throws NotFoundException;
}
