package com.askwinston.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionExpiringReport {
    private Long userId;
    private String userName;
    private String email;
    private String phoneNumber;
    private Long subscriptionId;
    private String subscriptionDate;
    private String subscriptionExpiryDate;
    private Integer totalRefills;
    private Integer refillsLeft;
}
