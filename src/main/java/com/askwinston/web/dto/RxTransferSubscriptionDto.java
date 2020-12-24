package com.askwinston.web.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RxTransferSubscriptionDto {

    @JsonView(DtoView.UserVisibility.class)
    private String promoCode;
    @JsonView(DtoView.UserVisibility.class)
    private Long rxDocumentId;
    @JsonView(DtoView.UserVisibility.class)
    private String rxNumber;
    @JsonView(DtoView.UserVisibility.class)
    private String pharmacyNameAndAddress;
    @JsonView(DtoView.UserVisibility.class)
    private String pharmacyPhone;
    @JsonView(DtoView.UserVisibility.class)
    private Long productId;
    @JsonView(DtoView.UserVisibility.class)
    private Long productQuantityId;
}
