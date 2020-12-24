package com.askwinston.web.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
public class OptionDto {

    @JsonView(DtoView.PatientVisibility.class)
    private String label;

    @JsonView(DtoView.PatientVisibility.class)
    private String value;
}
