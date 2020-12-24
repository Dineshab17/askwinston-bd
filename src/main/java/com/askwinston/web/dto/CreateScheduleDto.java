package com.askwinston.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateScheduleDto {

    private List<TimeIntervalDto> events;
    private boolean isRecurring;
}
