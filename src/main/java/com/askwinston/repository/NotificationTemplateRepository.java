package com.askwinston.repository;

import com.askwinston.web.dto.NotificationTemplatePersistenceDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends CrudRepository<NotificationTemplatePersistenceDto, Long> {

    List<NotificationTemplatePersistenceDto> findAllOrOrderByActive(boolean active);

    List<NotificationTemplatePersistenceDto> findAllByNotificationEventTypeNameAndActive(String name, boolean active);

    Optional<NotificationTemplatePersistenceDto> findByName(String name);
}