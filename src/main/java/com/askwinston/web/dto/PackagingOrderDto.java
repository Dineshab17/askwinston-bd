package com.askwinston.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackagingOrderDto {
    private String text;
    private Long amount;
    private LocalDate toDate;
    private int refillsLeft;
    private String rxNumber;
}
