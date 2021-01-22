package com.askwinston.service.impl;

import com.askwinston.model.Inline;
import com.askwinston.model.Province;
import com.askwinston.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.*;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    private static final EnumSet<Province> EAST_PROVINCES = EnumSet.of(Province.ON, Province.NB,
            Province.NS, Province.NL, Province.PE);
    private static final EnumSet<Province> CENTER_PROVINCES = EnumSet.of(Province.BC, Province.AB);
    private static final EnumSet<Province> WEST_PROVINCES = EnumSet.of(Province.MB, Province.SK);

    @Value("${askwinston.email.from}")
    private String askwinstonEmailFrom;

    @Value("${askwinston.email.info}")
    private String askwinstonEmailInfo;

    @Value("${askwinston.domain.url}")
    private String askwinstonDomainUrl;

    @Value("${askwinston.token.reset-password.expire-in-seconds}")
    private int resetPasswordTokenExpirationSeconds;

    @Value("${askwinston.pharmacy.email.east}")
    private String pharmacyEmailEast;

    @Value("${askwinston.pharmacy.email.center}")
    private String pharmacyEmailCenter;

    @Value("${askwinston.pharmacy.email.west}")
    private String pharmacyEmailWest;

    private final JavaMailSender emailSender;

    @Override
    public String getEmailInfo() {
        return askwinstonEmailInfo;
    }

    public EmailServiceImpl(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * @param email
     * @param token
     * To send reset password email to the user
     */
    @Override
    public void sendResetPasswordEmail(String email, String token) {
        try {
            String subject = "You have requested the password reset";

            ClassPathResource resource = new ClassPathResource("email/new/resetPassword.html");
            String template = StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
            DecimalFormat df = new DecimalFormat("###");

            HashMap<String, String> templateVars = new HashMap<>();
            templateVars.put("resetUrl", askwinstonDomainUrl + "/forgot-password?token=" + token);
            templateVars.put("linkExpirationHours", df.format((double) resetPasswordTokenExpirationSeconds / 3600.0));

            String html = substituteValues(template, templateVars);

            List<Inline> inlines = new ArrayList<>();
            inlines.add(new Inline("logo", new ClassPathResource("email/images/logo.png")));
            inlines.add(new Inline("webAddressIcon", new ClassPathResource("email/images/web_address_icon.png")));
            inlines.add(new Inline("emailAddressIcon", new ClassPathResource("email/images/email_address_icon.png")));
            send(email, subject, html, inlines);
        } catch (Exception e) {
            log.error("sending message failed", e);
        }
    }

    /**
     * @param billingProvince
     * @return String
     * To choose pharmacy email based on the province
     */
    @Override
    public String choosePharmacyEmail(Province billingProvince) {
        if (EAST_PROVINCES.contains(billingProvince)) {
            return pharmacyEmailEast;
        } else if (CENTER_PROVINCES.contains(billingProvince)) {
            return pharmacyEmailCenter;
        } else if (WEST_PROVINCES.contains(billingProvince)) {
            return pharmacyEmailWest;
        } else {
            return pharmacyEmailEast;
        }
    }

    /**
     * @param template
     * @param templateVars
     * @return String
     * To substitute values in the email templates
     */
    private String substituteValues(String template, HashMap<String, String> templateVars) {
        for (Map.Entry<String, String> entry : templateVars.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }

    /**
     * Thread to send the email
     */
    private static class SendEmailTask implements Runnable {

        JavaMailSender emailSender;
        MimeMessage message;

        public SendEmailTask(JavaMailSender emailSender, MimeMessage message) {
            this.emailSender = emailSender;
            this.message = message;
        }

        @Override
        public void run() {
            emailSender.send(message);
        }
    }

    /**
     * @param to
     * @param subject
     * @param html
     * @param inlines
     * @throws MessagingException
     * To send notification emails to the user
     */
    @Override
    public void send(String to, String subject, String html, List<Inline> inlines) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(askwinstonEmailFrom);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        for (Inline inline : inlines) {
            helper.addInline(inline.getCid(), inline.getResource());
        }
        new Thread(new SendEmailTask(emailSender, message)).start();
        log.info("Notification sent. To: " + to + ", Subject: " + subject);
    }

}