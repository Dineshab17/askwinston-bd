package com.askwinston.notification;

import com.askwinston.model.Notification;
import com.askwinston.model.NotificationModel;
import com.askwinston.model.NotificationTemplate;
import com.askwinston.notification.mapper.EntityModelMapper;
import com.askwinston.service.EmailService;
import com.askwinston.service.NotificationService;
import com.askwinston.service.NotificationTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/*
    Class that generates, fills templates and sends notifications
 */

@Slf4j
@Service
@EnableScheduling
public class NotificationEngine {

    private NotificationService notificationService;
    private NotificationTemplateService notificationTemplateService;
    private EmailService emailService;

    private Map<Class<?>, EntityModelMapper<?>> mappersMap = new HashMap<>();
    private Map<Class<?>, CrudRepository<?, Long>> repositoryMap = new HashMap<>();

    public NotificationEngine(EmailService emailService,
                              NotificationService notificationService,
                              NotificationTemplateService notificationTemplateService,
                              List<EntityModelMapper<?>> mappers, List<CrudRepository<?, Long>> repositories) {
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.notificationTemplateService = notificationTemplateService;
        init(mappers, repositories);
    }

    private void init(List<EntityModelMapper<?>> mappers, List<CrudRepository<?, Long>> repositories) {
        mappers.forEach(mapper -> {
            Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(mapper.getClass(), EntityModelMapper.class);
            Class<?> entityClass = typeArguments[0];
            mappersMap.put(entityClass, mapper);
        });
        repositories.forEach(repository -> {
            Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(repository.getClass(), CrudRepository.class);
            Class<?> entityClass = typeArguments[0];
            if (mappersMap.containsKey(entityClass))
                repositoryMap.put(entityClass, repository);
        });
    }

    public <T extends Notifiable> void notify(NotificationEventType<T> eventType, T entity) {
        notify(eventType, entity, new HashMap<>());
    }

    @Transactional
    public <T extends Notifiable> void notify(NotificationEventType<T> eventType, T entity, Map<String, String> additionalParams) {
        List<NotificationTemplate> templates = notificationTemplateService.findAllByNotificationEventTypeAndActive(eventType, true);
        List<Notification> notifications = new ArrayList<>();
        templates.forEach(template -> {
            if (template.isDeferred()) {
                notifications.add(registerPlannedNotification(template, entity));
            } else {
                notifications.add(performNotification(eventType, entity, template, additionalParams));
            }
        });
        notificationService.saveAll(notifications);
    }

    @Scheduled(cron = "${notification.cron:0 0 12 * * *}")
    void checkPlannedNotifications() {
        List<Notification> notifications = notificationService.findAllByStatusAndDateBefore(Notification.Status.PLANNED, Date.from(Instant.now()));
        List<Notification> performedNotifications = notifications.stream()
                .filter(notification -> notification.getTemplate().isActive() && notification.getTemplate().isDeferred())
                .map(this::performDeferredNotification)
                .collect(Collectors.toList());
        notificationService.saveAll(performedNotifications);
    }

    private <T extends Notifiable> Notification registerPlannedNotification(NotificationTemplate template, T entity) {
        Notification notification = new Notification();
        notification.setTemplate(template);
        notification.setTargetEntityId(entity.getId());

        Calendar cal = Calendar.getInstance();
        cal.setTime(Date.from(Instant.now()));
        cal.add(Calendar.DATE, template.getDaysAfter());
        notification.setDate(cal.getTime());
        notification.setStatus(Notification.Status.PLANNED);
        return notification;
    }

    private <T extends Notifiable> Notification performDeferredNotification(Notification notification) {
        NotificationTemplate template = notification.getTemplate();
        NotificationEventType<T> eventType = (NotificationEventType<T>) template.getNotificationEventType();
        Optional<T> optional = getRepository(eventType.getConsumedEntityClass()).findById(notification.getTargetEntityId());
        if (optional.isPresent()) {
            T entity = optional.get();
            notification = performNotification(eventType, entity, template, new HashMap<>());
        } else {
            notification.setStatus(Notification.Status.FAILED);
        }
        return notification;
    }

    private <T extends Notifiable> Notification performNotification(NotificationEventType<T> eventType, T entity,
                                                                    NotificationTemplate template,
                                                                    Map<String, String> additionalParams) {
        EntityModelMapper<T> mapper = getEntityMapper(eventType.getConsumedEntityClass());
        NotificationModel model = new NotificationModel();
        mapper.doMap(entity, model, additionalParams);
        model.setEmail(eventType.getEmailProvider().apply(Pair.of(entity, template.getTarget())));
        String subject = fillTemplate(template.getSubjectTemplate(), model.getMap());
        String html = fillTemplate(template.getHtmlTemplate(), model.getMap());
        Notification notification = new Notification();
        notification.setEmail(model.getEmail());
        notification.setTemplate(template);
        notification.setDate(Date.from(Instant.now()));
        try {
            emailService.send(model.getEmail(), subject, html, model.getInlines());
            notification.setStatus(Notification.Status.SENT);
        } catch (MessagingException e) {
            log.error("Sending message failed", e);
            notification.setStatus(Notification.Status.FAILED);
        }
        return notification;
    }

    private String fillTemplate(String template, Map<String, String> templateVars) {
        String filledTemplate = template;
        for (Map.Entry<String, String> entry : templateVars.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                filledTemplate = filledTemplate.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return filledTemplate;
    }

    public <T extends Notifiable> EntityModelMapper<T> getEntityMapper(Class<T> entityClass) {
        EntityModelMapper<T> mapper = (EntityModelMapper<T>) mappersMap.get(entityClass);
        if (mapper == null)
            throw new IllegalArgumentException("No EntityModelMapper for class " + entityClass.getSimpleName());
        return mapper;
    }

    private <T extends Notifiable> CrudRepository<T, Long> getRepository(Class<T> entityClass) {
        CrudRepository<T, Long> repository = (CrudRepository<T, Long>) repositoryMap.get(entityClass);
        if (repository == null)
            throw new IllegalArgumentException("No repository for class " + entityClass.getSimpleName());
        return repository;
    }

}