package com.askwinston.model;

import com.askwinston.web.dto.DtoView;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Option {

    @JsonView(DtoView.PatientVisibility.class)
    private String label;

    @JsonView(DtoView.PatientVisibility.class)
    private String value;
}
