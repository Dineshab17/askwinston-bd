package com.askwinston.service.impl;

import com.askwinston.exception.NotFoundException;
import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.NotificationTemplate;
import com.askwinston.notification.NotificationEventType;
import com.askwinston.repository.NotificationTemplateRepository;
import com.askwinston.service.NotificationTemplateService;
import com.askwinston.web.dto.NotificationTemplatePersistenceDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

@Service
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private static final String NOTIFICATION_TEMPLATE_NOT_FOUND_ERROR_MESSAGE = "NotificationTemplate not found";

    private NotificationTemplateRepository repository;
    private ParsingHelper parsingHelper;

    public NotificationTemplateServiceImpl(NotificationTemplateRepository repository,
                                           ParsingHelper parsingHelper) {
        this.repository = repository;
        this.parsingHelper = parsingHelper;
    }

    @Transactional
    @Override
    public NotificationTemplate findById(long id) throws NotFoundException {
        NotificationTemplatePersistenceDto dto = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOTIFICATION_TEMPLATE_NOT_FOUND_ERROR_MESSAGE));
        return parsingHelper.mapObject(dto, NotificationTemplate.class);
    }

    @Transactional
    @Override
    public List<NotificationTemplate> findAll() {
        List<NotificationTemplatePersistenceDto> dtoList = new LinkedList<>();
        repository.findAll().forEach(dtoList::add);
        return parsingHelper.mapObjects(dtoList, NotificationTemplate.class);
    }

    @Transactional
    @Override
    public List<NotificationTemplate> findAllByNotificationEventTypeAndActive(NotificationEventType<?> type, boolean active) {
        List<NotificationTemplatePersistenceDto> dtoList = repository.findAllByNotificationEventTypeNameAndActive(type.getName(), active);
        return parsingHelper.mapObjects(dtoList, NotificationTemplate.class);
    }

    @Transactional
    @Override
    public NotificationTemplate findByName(String name) throws NotFoundException {
        NotificationTemplatePersistenceDto dto = repository.findByName(name)
                .orElseThrow(() -> new NotFoundException(NOTIFICATION_TEMPLATE_NOT_FOUND_ERROR_MESSAGE));
        return parsingHelper.mapObject(dto, NotificationTemplate.class);
    }

    @Transactional
    @Override
    public NotificationTemplate save(NotificationTemplate template) {
        NotificationTemplatePersistenceDto dto = repository.save(parsingHelper.mapObject(template, NotificationTemplatePersistenceDto.class));
        return parsingHelper.mapObject(dto, NotificationTemplate.class);
    }

    @Transactional
    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }

    @Transactional
    @Override
    public NotificationTemplate deleteById(Long id) throws NotFoundException {
        NotificationTemplatePersistenceDto dto = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOTIFICATION_TEMPLATE_NOT_FOUND_ERROR_MESSAGE));
        NotificationTemplate template = parsingHelper.mapObject(dto, NotificationTemplate.class);
        repository.deleteById(id);
        return template;
    }
}
