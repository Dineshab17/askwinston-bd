package com.askwinston.web.dto;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    @JsonView(DtoView.UserVisibility.class)
    private Long productId;
    @JsonView(DtoView.UserVisibility.class)
    private Long productQuantityId;
    @JsonView(DtoView.UserVisibility.class)
    private int productCount;
}
