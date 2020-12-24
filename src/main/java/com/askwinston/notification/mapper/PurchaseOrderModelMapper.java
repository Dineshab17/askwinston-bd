package com.askwinston.notification.mapper;

import com.askwinston.model.*;
import com.askwinston.service.DateService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/*
    Class that assembles NotificationModel for notifications, related to PurchaseOrder entity
 */

@Slf4j
@Component
public class PurchaseOrderModelMapper implements EntityModelMapper<PurchaseOrder> {

    @Value("${askwinston.domain.url}")
    private String baseUrl;

    private DateService dateService;

    public PurchaseOrderModelMapper(DateService dateService) {
        this.dateService = dateService;
    }

    @Override
    public void doMap(PurchaseOrder order, NotificationModel model, Map<String, String> additionalParams) {
        model.getMap().put("baseUrl", baseUrl);

        User patient = order.getUser();
        User doctor = order.getDoctor();
        String numberStr = order.getNumber() < 10 ? "0" + order.getNumber().toString() : order.getNumber().toString();
        String subNumberStr = order.getSubNumber() < 10 ? "0" + order.getSubNumber().toString() : order.getSubNumber().toString();
        model.getMap().put("orderId", numberStr + "-" + subNumberStr);
        model.getMap().put("service", "Xpresspost");
        if (order.getShippingTrackingNumber() != null)
            model.getMap().put("trackingNumber", order.getShippingTrackingNumber());
        if (order.getCourier() != null) {
            model.getMap().put("courier", order.getCourier().getName());
            model.getMap().put("courierLInk", order.getCourier().getLink());
            model.getMap().put("courierShortName", order.getCourier().getShortName());
        }
        model.getMap().put("doctorName", doctor.getFirstName() + " " + doctor.getLastName());
        model.getMap().put("patientName", patient.getFirstName() + " " + patient.getLastName());
        model.getMap().put("patientBirthday", dateService.formatBirthday(patient.getBirthday()));
        model.getMap().put("patientPhone", patient.getPhone());
        model.getMap().put("patientId", patient.getId().toString());

        String orderDate = dateService.formatDateTime(order.getDate(), patient.getTimezone());

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.CANADA);
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.applyLocalizedPattern("###.00");

        model.getMap().put("orderDate", orderDate);
        model.getMap().put("productsInfo", order.getItems().stream()
                .map(PurchaseOrderItem::getProductDosage)
                .collect(Collectors.joining("<br />")));
        model.getMap().put("totalPrice", decimalFormat.format((double) order.getOrderPrice() / 100.0));
        model.getMap().put("subTotal", decimalFormat.format((double) order.getCartPrice() / 100.0));
        String coPayText;
        if (order.getUser().getInsuranceDocument() != null && order.getCoPay() != null) {
            coPayText = order.getCoPay() == 0 ? "0.00" : new BigDecimal(order.getCoPay()).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN).toString();
        } else {
            coPayText = new BigDecimal(order.getOrderPrice()).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN).toString();
        }
        String coveredByInsuranceText;
        if (order.getUser().getInsuranceDocument() != null && order.getCoPay() != null) {
            String coveredByInsuranceAmountText = new BigDecimal(order.getOrderPrice() - order.getCoPay()).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN).toString();
            coveredByInsuranceText = "<p>Covered by insurance: $" + coveredByInsuranceAmountText + "</p>\n";
        } else {
            coveredByInsuranceText = "";
        }
        model.getMap().put("coveredByInsurance", coveredByInsuranceText);
        model.getMap().put("coPayText", coPayText);
        model.getMap().put("tax", formatLongPrice(order.getTaxes()));
        model.getMap().put("billingInfo", order.getBillingInfo() == null ? "" : order.getBillingInfo());

        String shippingInfo = order.getShippingAddressLine1() == null ? "" : order.getShippingAddressLine1();
        if (!StringUtils.isEmpty(order.getShippingAddressLine2())) {
            shippingInfo += ", " + order.getShippingAddressLine2();
        }
        shippingInfo += "<br />" + order.calculateShippingAddressLine3();
        model.getMap().put("shippingInfo", shippingInfo);
        String patientsAddress = "";
        if (order.getBillingAddressLine1() != null) {
            patientsAddress += order.getBillingAddressLine1();
        }
        if ((order.getBillingAddressLine2() != null) && !order.getBillingAddressLine2().isEmpty()) {
            patientsAddress += ", " + order.getBillingAddressLine2();
        }
        patientsAddress += ", " + order.calculateBillingAddressLine3();

        model.getMap().put("dateOfBirth", dateService.formatBirthday(patient.getBirthday()));
        model.getMap().put("patientAddress", patientsAddress);


        //ProductTable

        String productTable = order.getItems().stream()
                .map(o -> "<tr  class=\"pharm\"><td class=\"pharm\">" + o.getProductName() + "</td>"
                        + "<td class=\"pharm dosage\">" + o.getProductDosage() + "</td>"
                        + "<td class=\"pharm\">" + order.getPrescription().getRefillsLeft() + "</td>"
                        + "<td class=\"pharm\">" + formatLongPrice(o.getProductPrice()) + "</td></tr>")
                .collect(Collectors.joining("\n                    "));
        model.getMap().put("productTable", productTable);

        String pharmacyId = order.getSubscription().getPharmacyId();
        model.getMap().put("pharmacyId", blank(pharmacyId));

        Prescription prescription = order.getPrescription();
        if (prescription != null) {
            int refills = prescription.getRefills();
            int refillsLeft = prescription.getRefillsLeft();
            String title = "Order #" + numberStr + "-" + subNumberStr;
            model.getMap().put("refillNumber", String.valueOf((refills - refillsLeft)));
            model.getMap().put("title", title);
            model.getMap().put("refillsLeft", String.valueOf(refillsLeft));
            String prescriptionDate = dateService.formatDateTime(prescription.getDate(), doctor.getTimezone());
            model.getMap().put("dateOfPrescription", prescriptionDate);
        }
        if (additionalParams != null) {
            model.getMap().putAll(additionalParams);
        }
        model.getInlines().add(new Inline("logo", new ClassPathResource("email/images/logo.png")));
        model.getInlines().add(new Inline("webAddressIcon", new ClassPathResource("email/images/web_address_icon.png")));
        model.getInlines().add(new Inline("emailAddressIcon", new ClassPathResource("email/images/email_address_icon.png")));
    }

    private String formatLongPrice(Long price) {
        String orderPriceString = price.toString();
        StringBuilder priceStringBuilder = new StringBuilder("$");
        int offset = orderPriceString.length() > 3 ? 2 : orderPriceString.length() > 2 ? 1 : 0;
        if (offset > 0) {
            priceStringBuilder.append(orderPriceString, 0, orderPriceString.length() - offset);
            priceStringBuilder.append(".");
            priceStringBuilder.append(orderPriceString.substring(orderPriceString.length() - offset));
        } else {
            priceStringBuilder.append(orderPriceString).append(".00");
        }
        return priceStringBuilder.toString();
    }

    private String blank(Object o) {
        return o == null ? "" : o.toString();
    }
}
