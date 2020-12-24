package com.askwinston.notification.mapper;

import com.askwinston.model.Inline;
import com.askwinston.model.NotificationModel;
import com.askwinston.model.User;
import com.askwinston.service.DateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/*
    Class that assembles NotificationModel for notifications, related to User entity
 */

@Slf4j
@Component
public class UserModelMapper implements EntityModelMapper<User> {

    private DateService dateService;

    @Value("${askwinston.domain.url}")
    private String baseUrl;

    public UserModelMapper(DateService dateService) {
        this.dateService = dateService;
    }

    @Override
    public void doMap(User patient, NotificationModel model, Map<String, String> additionalParams) {
        model.setEmail(patient.getEmail());
        model.getMap().put("firstName", patient.getFirstName());
        model.getMap().put("lastName", patient.getLastName());
        model.getMap().put("birthday", dateService.formatBirthday(patient.getBirthday()));
        model.getMap().put("phone", patient.getPhone());
        model.getMap().put("email", patient.getEmail());
        model.getMap().put("province", patient.getProvince().toString());
        model.getMap().put("baseUrl", baseUrl);

        // Masked email string
        int atChar = patient.getEmail().indexOf("@");
        StringBuilder builder = new StringBuilder(patient.getEmail());
        builder.replace(2, atChar, "*******");
        model.getMap().put("emailMask", builder.toString());

        // Formatted current date and time
        String formattedDate = (new SimpleDateFormat("MM/dd/yyyy HH:mm a ('UTC')")).format(new Date());
        model.getMap().put("formattedDateTime", formattedDate);

        model.getMap().putAll(additionalParams);

        model.getInlines().add(new Inline("logo", new ClassPathResource("email/images/logo.png")));
        model.getInlines().add(new Inline("webAddressIcon", new ClassPathResource("email/images/web_address_icon.png")));
        model.getInlines().add(new Inline("emailAddressIcon", new ClassPathResource("email/images/email_address_icon.png")));
    }
}