package com.askwinston.web.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrescriptionDto {
    @JsonView(DtoView.UserVisibility.class)
    private Long id;
    @JsonView(DtoView.UserVisibility.class)
    private Date date;
    @JsonView(DtoView.UserVisibility.class)
    private Date toDate;
    @JsonView(DtoView.UserVisibility.class)
    private int refills;
    @JsonView(DtoView.UserVisibility.class)
    private int refillsLeft;
}
