package com.askwinston.web.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingCardDto {
    @JsonView(DtoView.UserVisibility.class)
    private String id;

    private String token;

    @JsonView(DtoView.UserVisibility.class)
    private String last4;
    @JsonView(DtoView.UserVisibility.class)
    private String brand;
    @JsonView(DtoView.UserVisibility.class)
    private Long expMonth;
    @JsonView(DtoView.UserVisibility.class)
    private Long expYear;
    @JsonView(DtoView.UserVisibility.class)
    private String addressLine1;
    @JsonView(DtoView.UserVisibility.class)
    private String addressLine2;
    @JsonView(DtoView.UserVisibility.class)
    private String addressCity;
    @JsonView(DtoView.UserVisibility.class)
    private String addressProvince;
    @JsonView(DtoView.UserVisibility.class)
    private String addressPostalCode;
    @JsonView(DtoView.UserVisibility.class)
    private String addressCountry;
    @JsonView(DtoView.UserVisibility.class)
    private boolean isPrimary;
    @JsonView(DtoView.UserVisibility.class)
    private Boolean isValid;
}
