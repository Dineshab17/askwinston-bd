package com.askwinston.web.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfoDto {
    @JsonView(DtoView.UserVisibility.class)
    private String billingId;
    @JsonView(DtoView.UserVisibility.class)
    private Long shippingId;
    @JsonView(DtoView.UserVisibility.class)
    private String promoCode;
}
