package com.askwinston.model;

import lombok.Data;

/**
 * @author Vijay
 * @created 19-07-2022  - 11:06
 * @project askwinston
 */

@Data
public class SubscriptionRenewedReport {
    private Long userId;
    private String userName;
    private String email;
    private String address;
    private String product;
    private String phoneNumber;
    private Long expiredSubscriptionId;
    private String subscriptionExpiredDate;
    private Long renewedSubscriptionId;
    private String subscriptionRenewalDate;
    private Integer totalRefills;
    private Integer refillsLeft;
}
