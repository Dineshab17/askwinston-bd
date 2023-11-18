package com.askwinston.web.dto;

import com.askwinston.model.ProductQuantity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductQuantityDto {
    private Long id;

    @NotNull(groups = {CreateProductValidation.class})
    @Size(min = 2, max = 255, groups = {CreateProductValidation.class})
    private String quantity;

    @NotNull(groups = {CreateProductValidation.class})
    private long price;

    private int ordinal;

    private int isDefault;

    private String dosage;

    private ProductQuantity.Supply supply;

    private Boolean isSingleTimePurchaseAllowed;

    public interface CreateProductValidation {
    }
}
