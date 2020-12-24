package com.askwinston.web.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StayConnectedRecordDto {

    @JsonView(DtoView.HiddenVisibility.class)
    private Long id;
    @JsonView(DtoView.AdminVisibility.class)
    private String email;
    @JsonView(DtoView.AdminVisibility.class)
    private String name;
}
