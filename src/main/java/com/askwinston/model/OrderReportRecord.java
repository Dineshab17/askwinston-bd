package com.askwinston.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderReportRecord {

    private long id;
    private String customerName;
    private String dateOfBirth;
    private String email;
    private String phone;
    private String orderNumber;
    private String productAndDosage;
    private String amount;
    private String coPay;
    private boolean hasInsurance;
    private String orderDate;
    private String shippingDate;
    private String shippingAddress;
    private String trackingNumber;
    private String promoCode;
    private String billingCard;
    private String transactionId;
}
