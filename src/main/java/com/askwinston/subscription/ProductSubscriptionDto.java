package com.askwinston.subscription;

import com.askwinston.web.dto.*;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductSubscriptionDto {

    @JsonView(DtoView.UserVisibility.class)
    private Long id;

    @JsonView({DtoView.DoctorVisibility.class, DtoView.AdminVisibility.class})
    private UserDto user;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class, DtoView.AdminVisibility.class})
    private ProductSubscription.Status status;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class})
    private PrescriptionDto prescription;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class, DtoView.AdminVisibility.class})
    private List<ProductSubscriptionItemDto> items;

    @JsonView({DtoView.PatientVisibility.class, DtoView.AdminVisibility.class})
    private List<PurchaseOrderDto> orders;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class, DtoView.AdminVisibility.class})
    private LocalDateTime date;

    @JsonView(DtoView.PatientVisibility.class)
    private LocalDate nextOrderDate;

    @JsonView({DtoView.PatientVisibility.class, DtoView.AdminVisibility.class})
    private LocalDate pauseDate;

    @JsonView({DtoView.PatientVisibility.class, DtoView.AdminVisibility.class})
    private String notes;

    @JsonView(DtoView.PatientVisibility.class)
    private String finishNotes;

    @JsonView(DtoView.PatientVisibility.class)
    private String pharmacyId;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class})
    private long orderPrice;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class})
    private PromoCodeDto promoCode;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class})
    private String shippingAddressLine1;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class})
    private String shippingAddressLine2;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class})
    private String shippingAddressCity;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class})
    private String shippingAddressProvince;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class})
    private String shippingAddressPostalCode;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class})
    private String shippingAddressCountry;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class})
    private String billingInfo;

}
