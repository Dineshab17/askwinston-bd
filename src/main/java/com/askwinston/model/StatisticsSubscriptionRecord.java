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
public class StatisticsSubscriptionRecord {
    private long subscriptionId;
    private String subscriptionStatus;
    private String nextOrderDate;
    private List<StatisticsOrderRecord> orders = new ArrayList<>();
}
