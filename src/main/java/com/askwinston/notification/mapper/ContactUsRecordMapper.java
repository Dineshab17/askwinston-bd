package com.askwinston.notification.mapper;

import com.askwinston.model.ContactUsRecord;
import com.askwinston.model.Inline;
import com.askwinston.model.NotificationModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Map;

/*
    Class that assembles NotificationModel for notifications, related to ContactUsRecord entity
 */

@Slf4j
@Component
public class ContactUsRecordMapper implements EntityModelMapper<ContactUsRecord> {

    @Override
    public void doMap(ContactUsRecord entity, NotificationModel model, Map<String, String> additionalParams) {
        try {
            model.getMap().put("email", entity.getEmail());
            model.getMap().put("name", entity.getName());
            model.getMap().put("phone", entity.getPhone());
            model.getMap().put("message", entity.getMessage());

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        model.getMap().putAll(additionalParams);

        model.getInlines().add(new Inline("logo", new ClassPathResource("email/images/logo.png")));
        model.getInlines().add(new Inline("webAddressIcon", new ClassPathResource("email/images/web_address_icon.png")));
        model.getInlines().add(new Inline("emailAddressIcon", new ClassPathResource("email/images/email_address_icon.png")));
    }
}
