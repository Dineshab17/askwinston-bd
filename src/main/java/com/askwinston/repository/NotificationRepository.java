package com.askwinston.repository;

import com.askwinston.model.Notification;
import com.askwinston.web.dto.NotificationPersistenceDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<NotificationPersistenceDto, Long> {

    List<NotificationPersistenceDto> findAllByOrderByDateAsc();

    List<NotificationPersistenceDto> findAllByStatusAndDateBefore(Notification.Status status, Date date);

    List<NotificationPersistenceDto> deleteAllByTemplateId(Long id);
}
