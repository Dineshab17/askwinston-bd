package com.askwinston.web.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingAddressDto {
    @JsonView(DtoView.UserVisibility.class)
    private Long id;

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
}
