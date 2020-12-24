package com.askwinston.web.dto;

import com.askwinston.subscription.ProductSubscriptionDto;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorSlotDto {

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class, DtoView.AdminVisibility.class})
    private long id;

    @JsonView({DtoView.PatientVisibility.class, DtoView.AdminVisibility.class})
    private String doctor;

    @JsonView(DtoView.UserVisibility.class)
    private List<ProductSubscriptionDto> subscription;

    @JsonView({DtoView.DoctorVisibility.class, DtoView.AdminVisibility.class})
    private boolean isFree;
}
