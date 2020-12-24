package com.askwinston.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticsCustomerRecord {

    private long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userBirthday;
    private boolean hasValidCard;
    private List<StatisticsSubscriptionRecord> subscriptions = new ArrayList<>();
}
