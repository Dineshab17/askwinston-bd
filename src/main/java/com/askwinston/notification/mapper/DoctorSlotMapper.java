package com.askwinston.notification.mapper;

import com.askwinston.model.DoctorSlot;
import com.askwinston.model.Inline;
import com.askwinston.model.NotificationModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

/*
    Class that assembles NotificationModel for notifications, related to DoctorSlot entity
 */

@Component
public class DoctorSlotMapper implements EntityModelMapper<DoctorSlot> {
    @Override
    public void doMap(DoctorSlot entity, NotificationModel model, Map<String, String> additionalParams) {
        Locale en = Locale.forLanguageTag("en");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy", en);
        String dateString = entity.getDate().format(formatter);
        formatter = DateTimeFormatter.ofPattern("hh:mm a");
        String timeString = entity.getTime().format(formatter);
        model.getMap().put("appointmentDate", dateString + " at " + timeString);
        model.getMap().put("patientInfo", entity.getPatient().getFirstName() + " " + entity.getPatient().getLastName() + " " + entity.getPatient().getBirthday());
        model.getInlines().add(new Inline("logo", new ClassPathResource("email/images/logo.png")));
        model.getInlines().add(new Inline("webAddressIcon", new ClassPathResource("email/images/web_address_icon.png")));
        model.getInlines().add(new Inline("emailAddressIcon", new ClassPathResource("email/images/email_address_icon.png")));
    }
}
