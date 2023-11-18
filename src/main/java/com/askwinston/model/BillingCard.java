package com.askwinston.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingCard {
    @Id
    private String id;
    private String last4;

    private String brand;
    private Long expMonth;
    private Long expYear;

    private String addressLine1;
    private String addressLine2;
    private String addressCity;

    @Enumerated(EnumType.STRING)
    private Province addressProvince;
    private String addressPostalCode;
    private String addressCountry;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private boolean isPrimary = false;
    private Boolean isValid = null;
}
