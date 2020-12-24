package com.askwinston.web.controller;

import com.askwinston.exception.NotFoundException;
import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.Notification;
import com.askwinston.model.NotificationTemplate;
import com.askwinston.notification.NotificationEventType;
import com.askwinston.notification.NotificationEventTypeContainer;
import com.askwinston.service.NotificationService;
import com.askwinston.service.NotificationTemplateService;
import com.askwinston.web.dto.NotificationDto;
import com.askwinston.web.dto.NotificationEventTypeDto;
import com.askwinston.web.dto.NotificationTemplateDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private static final String TEMPLATE_NOT_FOUND_ERROR_MESSAGE = "Template not found";

    private NotificationService notificationService;
    private NotificationTemplateService notificationTemplateService;
    private ParsingHelper parsingHelper;

    public NotificationController(NotificationService notificationService,
                                  NotificationTemplateService notificationTemplateService,
                                  ParsingHelper parsingHelper) {
        this.notificationService = notificationService;
        this.notificationTemplateService = notificationTemplateService;
        this.parsingHelper = parsingHelper;
    }

    @GetMapping(value = "/template/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public NotificationTemplateDto getTemplate(@PathVariable(name = "id") Long id) {
        NotificationTemplate template = null;
        try {
            template = notificationTemplateService.findById(id);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, TEMPLATE_NOT_FOUND_ERROR_MESSAGE);
        }
        return parsingHelper.mapObject(template, NotificationTemplateDto.class);
    }

    @GetMapping(value = "/template")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<NotificationTemplateDto> getTemplates() {
        return parsingHelper.mapObjects(notificationTemplateService.findAll(), NotificationTemplateDto.class);
    }

    @PostMapping(value = "/template")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public NotificationTemplateDto addTemplate(@RequestBody NotificationTemplateDto templateDto) {
        NotificationTemplate template = parsingHelper.mapObject(templateDto, NotificationTemplate.class);
        template = notificationTemplateService.save(template);
        return parsingHelper.mapObject(template, NotificationTemplateDto.class);
    }

    @PutMapping(value = "/template")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public NotificationTemplateDto updateTemplate(@RequestBody NotificationTemplateDto templateDto) {
        NotificationTemplate template = parsingHelper.mapObject(templateDto, NotificationTemplate.class);
        if (!notificationTemplateService.existsById(template.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, TEMPLATE_NOT_FOUND_ERROR_MESSAGE);
        }
        notificationTemplateService.save(template);
        return parsingHelper.mapObject(template, NotificationTemplateDto.class);
    }

    @DeleteMapping(value = "/template/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public NotificationTemplateDto deleteTemplate(@PathVariable(name = "id") Long id) {
        NotificationTemplate template = null;
        try {
            template = notificationTemplateService.findById(id);
            notificationTemplateService.deleteById(id);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, TEMPLATE_NOT_FOUND_ERROR_MESSAGE);
        }
        return parsingHelper.mapObject(template, NotificationTemplateDto.class);
    }

    @GetMapping(value = "/type")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<NotificationEventTypeDto> getTypes() {
        List<NotificationEventType<?>> types = new ArrayList<>(NotificationEventTypeContainer.getValues().values());
        return parsingHelper.mapObjects(types, NotificationEventTypeDto.class);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR', 'PATIENT', 'PHARMACIST')")
    public NotificationDto getNotification(@PathVariable(name = "id") Long id) {
        Notification notification = null;
        try {
            notification = notificationService.findById(id);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }
        return parsingHelper.mapObject(notification, NotificationDto.class);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR', 'PATIENT', 'PHARMACIST')")
    public List<NotificationDto> getNotifications() {
        return parsingHelper.mapObjects(notificationService.findAll(), NotificationDto.class);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public NotificationDto deleteNotification(@PathVariable(name = "id") Long id) {
        Notification notification = null;
        try {
            notification = notificationService.findById(id);
            notificationService.deleteById(id);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }
        return parsingHelper.mapObject(notification, NotificationDto.class);
    }
}
