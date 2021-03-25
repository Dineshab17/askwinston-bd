package com.askwinston.web.dto;

import com.askwinston.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;

    @NotNull(groups = {CreateProductValidation.class})
    @Size(min = 2, max = 255, groups = {CreateProductValidation.class})
    private String name;

    private String subName;

    @NotNull(groups = {CreateProductValidation.class})
    @Size(min = 10, max = 2047, groups = {CreateProductValidation.class})
    private String description;

    @NotNull(groups = {CreateProductValidation.class})
    @Size(min = 10, max = 2047, groups = {CreateProductValidation.class})
    private String productPageText;

    private String safetyInfo;

    private String ingredient;

    private Product.Category category;

    private Product.ProblemCategory problemCategory;

    private boolean isCombo;

    private String startingAtPrice;

    private String startingAtForm;

    private String dosingTips;

    private List<ProductQuantityDto> quantities;

    private List<FaqDto> frequentlyAskedQuestions;

    public interface CreateProductValidation {
    }
}
