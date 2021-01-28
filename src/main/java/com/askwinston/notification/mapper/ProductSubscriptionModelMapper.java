package com.askwinston.notification.mapper;

import com.askwinston.model.Inline;
import com.askwinston.model.NotificationModel;
import com.askwinston.model.Prescription;
import com.askwinston.model.User;
import com.askwinston.service.DateService;
import com.askwinston.subscription.ProductSubscription;
import com.askwinston.subscription.ProductSubscriptionItem;
import com.askwinston.subscription.SubscriptionEngine;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

/*
    Class that assembles NotificationModel for notifications, related to ProductSubscription entity
 */

@Slf4j
@Component
@RefreshScope
public class ProductSubscriptionModelMapper implements EntityModelMapper<ProductSubscription> {

    @Value("${askwinston.domain.url}")
    private String baseUrl;

    private DateService dateService;

    public ProductSubscriptionModelMapper(DateService dateService) {
        this.dateService = dateService;
    }

    @Override
    public void doMap(ProductSubscription subscription, NotificationModel model, Map<String, String> additionalParams) {
        if (subscription != null) {
            User patient = subscription.getUser();
            User doctor = subscription.getDoctor();

            model.getMap().put("baseUrl", baseUrl);

            model.getMap().put("subscriptionId", subscription.getId().toString());
            model.getMap().put("courier", "Canada Post");
            model.getMap().put("service", "Xpresspost");
            model.getMap().put("patientName", patient.getFirstName() + " " + patient.getLastName());
            model.getMap().put("patientBirthday", dateService.formatBirthday(patient.getBirthday()));
            model.getMap().put("patientPhone", patient.getPhone());
            model.getMap().put("patientId", patient.getId().toString());

            String subscriptionDate = dateService.formatDateTime(Date.from(Instant.now()), patient.getTimezone());
            model.getMap().put("subscriptionDate", subscriptionDate);

            if (!subscription.getUser().getBillingCards().isEmpty()) {
                String patientsAddress = "";
                if (subscription.getBillingCard() != null) {
                    model.getMap().put("billingInfo", "**** **** **** " + subscription.getBillingCard().getLast4());
                    if (subscription.getBillingCard().getAddressLine1() != null) {
                        patientsAddress += subscription.getBillingCard().getAddressLine1();
                    }
                    if ((subscription.getBillingCard().getAddressLine2() != null) && !subscription.getBillingCard().getAddressLine1().isEmpty()) {
                        patientsAddress += ", " + subscription.getBillingCard().getAddressLine1();
                    }
                }
                patientsAddress += ", " + subscription.calculateBillingAddressLine3();
                model.getMap().put("patientAddress", patientsAddress);

            }


            String shippingInfo = subscription.getShippingAddress().getAddressLine1();
            if (!StringUtils.isEmpty(subscription.getShippingAddress().getAddressLine2())) {
                shippingInfo += ", " + subscription.getShippingAddress().getAddressLine2();
            }
            shippingInfo += "<br />" + subscription.calculateShippingAddressLine3();
            model.getMap().put("shippingInfo", shippingInfo);


            model.getMap().put("dateOfBirth", dateService.formatBirthday(patient.getBirthday()));
            String orderPriceString = subscription.getOrderPrice().toString();
            StringBuilder orderPriceStringBuilder = new StringBuilder(orderPriceString.substring(0, orderPriceString.length() - 2));
            orderPriceStringBuilder.append(".");
            orderPriceStringBuilder.append(orderPriceString.substring(orderPriceString.length() - 2));
            model.getMap().put("orderPrice", orderPriceStringBuilder.toString());
            model.getMap().put("productsInfo", subscription.getItems().get(0).getProductDosage());

            ProductSubscriptionItem item = subscription.getItems().get(0);
            if (item != null) {
                model.getMap().put("dragNameAndDosage", item.getProductName() + " " + item.getProductDosage());
                model.getMap().put("productDosage", item.getProductDosage());
                model.getMap().put("productName", item.getProductName());
            }

            if (subscription.getDoctor() != null)
                model.getMap().put("doctorName", doctor.getFirstName() + " " + doctor.getLastName());


            if (subscription.getStatus().equals(ProductSubscription.Status.ACTIVE)) {
                model.getMap().put("nextOrderDate", subscription.getNextOrderDate().toString());
                model.getMap().put("orderProcessingDate", subscription.getNextOrderDate().minusDays(SubscriptionEngine.getDeliveryDays()).toString());
            }


            Prescription prescription = subscription.getPrescription();
            if (prescription != null && prescription.getToDate() != null) {
                int refills = prescription.getRefills();
                int refillsLeft = prescription.getRefillsLeft();
                model.getMap().put("refillNumber", String.valueOf((refills - refillsLeft)));
                model.getMap().put("refillsLeft", String.valueOf(refillsLeft));

                String prescriptionDate = dateService.formatDateTime(prescription.getDate(), doctor.getTimezone());
                model.getMap().put("dateOfPrescription", prescriptionDate);
                String prescriptionToDate = dateService.formatDateTime(prescription.getToDate(), doctor.getTimezone());
                model.getMap().put("prescriptionToDate", prescriptionToDate);
            }
            model.getMap().putAll(additionalParams);

            model.getInlines().add(new Inline("logo", new ClassPathResource("email/images/logo.png")));
            model.getInlines().add(new Inline("webAddressIcon", new ClassPathResource("email/images/web_address_icon.png")));
            model.getInlines().add(new Inline("emailAddressIcon", new ClassPathResource("email/images/email_address_icon.png")));
        }
    }
}
