package com.askwinston.web.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemDto {
    @JsonView(DtoView.UserVisibility.class)
    private String productDosage;
    @JsonView(DtoView.UserVisibility.class)
    private long productPrice;
    @JsonView(DtoView.UserVisibility.class)
    private int productCount;
    @JsonView(DtoView.UserVisibility.class)
    private String productName;
}
