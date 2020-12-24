package com.askwinston.web.dto;

import com.askwinston.model.Product;
import com.askwinston.model.PromoCode;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeDto {
    @JsonView(DtoView.UserVisibility.class)
    private String code;
    @JsonView(DtoView.UserVisibility.class)
    private PromoCode.Type type;
    @JsonView(DtoView.UserVisibility.class)
    private Long value;
    @JsonView(DtoView.UserVisibility.class)
    private LocalDate fromDate;
    @JsonView(DtoView.UserVisibility.class)
    private LocalDate toDate;
    @JsonView(DtoView.UserVisibility.class)
    private List<Product.ProblemCategory> problemCategory;
}
