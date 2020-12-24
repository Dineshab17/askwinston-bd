package com.askwinston.web.dto;

import com.askwinston.model.Option;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
public class RxTransferStateRecordDto {

    @JsonView(DtoView.HiddenVisibility.class)
    private Long userId;

    @JsonView(DtoView.PatientVisibility.class)
    private int step;

    @JsonView(DtoView.PatientVisibility.class)
    private Option productCategory;

    @JsonView(DtoView.PatientVisibility.class)
    private Option product;

    @JsonView(DtoView.PatientVisibility.class)
    private Option quantity;

    @JsonView(DtoView.PatientVisibility.class)
    private String rxNumber;

    @JsonView(DtoView.PatientVisibility.class)
    private String pharmacyName;

    @JsonView(DtoView.PatientVisibility.class)
    private String pharmacyPhone;

    @JsonView(DtoView.PatientVisibility.class)
    private String bottleImageId;

}
