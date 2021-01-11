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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/notification")
@Slf4j
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

    /**
     * @param id
     * @return NotificationTemplateDto
     * To get the notification template by template id
     */
    @GetMapping(value = "/template/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public NotificationTemplateDto getTemplate(@PathVariable(name = "id") Long id) {
        NotificationTemplate template = null;
        try {
            template = notificationTemplateService.findById(id);
        } catch (NotFoundException e) {
            log.error("Template with id {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, TEMPLATE_NOT_FOUND_ERROR_MESSAGE);
        }
        return parsingHelper.mapObject(template, NotificationTemplateDto.class);
    }

    /**
     * @return List<NotificationTemplateDto>
     * To get all the notification templates
     */
    @GetMapping(value = "/template")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<NotificationTemplateDto> getTemplates() {
        return parsingHelper.mapObjects(notificationTemplateService.findAll(), NotificationTemplateDto.class);
    }

    /**
     * @param templateDto
     * @return NotificationTemplateDto
     * To add new notification template
     */
    @PostMapping(value = "/template")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public NotificationTemplateDto addTemplate(@RequestBody NotificationTemplateDto templateDto) {
        NotificationTemplate template = parsingHelper.mapObject(templateDto, NotificationTemplate.class);
        template = notificationTemplateService.save(template);
        log.info("Notification template with id {} has been saved", template.getId());
        return parsingHelper.mapObject(template, NotificationTemplateDto.class);
    }


    /**
     * @param templateDto
     * @return NotificationTemplateDto
     * To update existing notification templates
     */
    @PutMapping(value = "/template")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public NotificationTemplateDto updateTemplate(@RequestBody NotificationTemplateDto templateDto) {
        NotificationTemplate template = parsingHelper.mapObject(templateDto, NotificationTemplate.class);
        if (!notificationTemplateService.existsById(template.getId())) {
            log.error("Notification template with id {} not found", templateDto.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, TEMPLATE_NOT_FOUND_ERROR_MESSAGE);
        }
        notificationTemplateService.save(template);
        return parsingHelper.mapObject(template, NotificationTemplateDto.class);
    }

    /**
     * @param id
     * @return NotificationTemplateDto
     * To delete notification template with template id
     */
    @DeleteMapping(value = "/template/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public NotificationTemplateDto deleteTemplate(@PathVariable(name = "id") Long id) {
        NotificationTemplate template = null;
        try {
            template = notificationTemplateService.findById(id);
            notificationTemplateService.deleteById(id);
        } catch (NotFoundException e) {
            log.error("Template with id {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, TEMPLATE_NOT_FOUND_ERROR_MESSAGE);
        }
        return parsingHelper.mapObject(template, NotificationTemplateDto.class);
    }

    /**
     * @return List<NotificationEventTypeDto>
     * To get all the notification event types
     */
    @GetMapping(value = "/type")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<NotificationEventTypeDto> getTypes() {
        List<NotificationEventType<?>> types = new ArrayList<>(NotificationEventTypeContainer.getValues().values());
        return parsingHelper.mapObjects(types, NotificationEventTypeDto.class);
    }


    /**
     * @param id
     * @return NotificationDto
     * To get notification by notification id
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR', 'PATIENT', 'PHARMACIST')")
    public NotificationDto getNotification(@PathVariable(name = "id") Long id) {
        Notification notification = null;
        try {
            notification = notificationService.findById(id);
        } catch (NotFoundException e) {
            log.error("Notification with id {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }
        return parsingHelper.mapObject(notification, NotificationDto.class);
    }

    /**
     * @return List<NotificationDto>
     * To get all the notifications
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR', 'PATIENT', 'PHARMACIST')")
    public List<NotificationDto> getNotifications() {
        return parsingHelper.mapObjects(notificationService.findAll(), NotificationDto.class);
    }

    /**
     * @param id
     * @return NotificationDto
     * To delete notification by notification id
     */
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public NotificationDto deleteNotification(@PathVariable(name = "id") Long id) {
        Notification notification = null;
        try {
            notification = notificationService.findById(id);
            notificationService.deleteById(id);
        } catch (NotFoundException e) {
            log.error("Notification with id {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
        }
        return parsingHelper.mapObject(notification, NotificationDto.class);
    }
}
