package com.askwinston.service.impl;

import com.askwinston.exception.NotFoundException;
import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.Notification;
import com.askwinston.repository.NotificationRepository;
import com.askwinston.service.NotificationService;
import com.askwinston.web.dto.NotificationPersistenceDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private NotificationRepository repository;
    private ParsingHelper parsingHelper;

    public NotificationServiceImpl(NotificationRepository repository, ParsingHelper parsingHelper) {
        this.repository = repository;
        this.parsingHelper = parsingHelper;
    }

    @Transactional
    @Override
    public Notification findById(long id) throws NotFoundException {
        NotificationPersistenceDto dto = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("NotificationTemplate not found"));
        return parsingHelper.mapObject(dto, Notification.class);
    }

    @Transactional
    @Override
    public List<Notification> findAll() {
        List<NotificationPersistenceDto> dtoList = repository.findAllByOrderByDateAsc();
        return parsingHelper.mapObjects(dtoList, Notification.class);
    }

    @Transactional
    @Override
    public List<Notification> findAllByStatusAndDateBefore(Notification.Status status, Date date) {
        List<NotificationPersistenceDto> dtoList = repository.findAllByStatusAndDateBefore(status, date);
        return parsingHelper.mapObjects(dtoList, Notification.class);
    }

    @Transactional
    @Override
    public Notification save(Notification notification) {
        NotificationPersistenceDto dto = parsingHelper.mapObject(notification, NotificationPersistenceDto.class);
        return parsingHelper.mapObject(repository.save(dto), Notification.class);
    }

    @Transactional
    @Override
    public void saveAll(List<Notification> notifications) {
        List<NotificationPersistenceDto> dtoList = parsingHelper.mapObjects(notifications, NotificationPersistenceDto.class);
        repository.saveAll(dtoList);
    }

    @Transactional
    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Transactional
    @Override
    public Notification deleteById(Long id) throws NotFoundException {
        NotificationPersistenceDto dto = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        Notification notification = parsingHelper.mapObject(dto, Notification.class);
        repository.deleteById(id);
        return notification;
    }
}
