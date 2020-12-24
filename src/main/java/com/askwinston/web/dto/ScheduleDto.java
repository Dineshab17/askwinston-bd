package com.askwinston.web.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class, DtoView.AdminVisibility.class})
    private LocalDate date;

    @JsonView({DtoView.PatientVisibility.class, DtoView.DoctorVisibility.class, DtoView.AdminVisibility.class})
    private Map<LocalTime, List<DoctorSlotDto>> schedule = new LinkedHashMap<>();
}
