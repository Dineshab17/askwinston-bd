package com.askwinston.service;

import com.askwinston.model.Inline;
import com.askwinston.model.Province;
import jakarta.mail.MessagingException;

import java.util.List;

public interface EmailService {

    void sendResetPasswordEmail(String email, String token);

    String choosePharmacyEmail(Province billingProvince);

    String getEmailInfo();

    void send(String to, String subject, String html, List<Inline> inlines) throws MessagingException;
}
