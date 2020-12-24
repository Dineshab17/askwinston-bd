package com.askwinston.web.dto;

import com.askwinston.model.Province;
import com.askwinston.model.PurchaseOrder;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderDto {
    @JsonView(DtoView.UserVisibility.class)
    private Long id;

    @JsonView(DtoView.UserVisibility.class)
    private Long number;
    @JsonView(DtoView.UserVisibility.class)
    private Long subNumber;

    @JsonView(DtoView.UserVisibility.class)
    private List<PurchaseOrderItemDto> items = new ArrayList<>();

    @JsonView(DtoView.UserVisibility.class)
    private Date date;

    @JsonView(DtoView.UserVisibility.class)
    private PrescriptionDto prescription;

    @JsonView(DtoView.UserVisibility.class)
    private String shippingAddressLine1;
    @JsonView(DtoView.UserVisibility.class)
    private String shippingAddressLine2;
    @JsonView(DtoView.UserVisibility.class)
    private String shippingAddressCity;
    @JsonView(DtoView.UserVisibility.class)
    private Province shippingAddressProvince;
    @JsonView(DtoView.UserVisibility.class)
    private String shippingAddressPostalCode;
    @JsonView(DtoView.UserVisibility.class)
    private String shippingAddressCountry;

    @JsonView(DtoView.UserVisibility.class)
    private String billingInfo;

    @JsonView(DtoView.UserVisibility.class)
    private long cartPrice;
    @JsonView(DtoView.UserVisibility.class)
    private long shippingPrice;
    @JsonView(DtoView.UserVisibility.class)
    private long taxes;
    @JsonView(DtoView.UserVisibility.class)
    private long discount;
    @JsonView(DtoView.UserVisibility.class)
    private long orderPrice;
    @JsonView(DtoView.UserVisibility.class)
    private long coPay;

    @JsonView(DtoView.UserVisibility.class)
    private PurchaseOrder.Status status;

    @JsonView(DtoView.PharmacistVisibility.class)
    private UserDto user;

    @JsonView(DtoView.UserVisibility.class)
    private String rejectionNotes;

    @JsonView(DtoView.UserVisibility.class)
    private String shippingTrackingNumber;

    @JsonView(DtoView.UserVisibility.class)
    private String shippingTrackingUrl;

    @JsonView(DtoView.PharmacistVisibility.class)
    private Long rxDocumentId;
    @JsonView(DtoView.PharmacistVisibility.class)
    private String rxTransferNumber;
    @JsonView(DtoView.PharmacistVisibility.class)
    private String pharmacyNameAndAddress;
    @JsonView(DtoView.PharmacistVisibility.class)
    private String pharmacyPhone;
    @JsonView(DtoView.PharmacistVisibility.class)
    private int maxRefillsNumber;
    @JsonView(DtoView.UserVisibility.class)
    private String promoCode;
    @JsonView(DtoView.AdminVisibility.class)
    private String courier;
}
