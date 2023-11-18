package com.askwinston.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaqDto {

    private Long id;

    private String question;

    @NotNull(groups = {CreateProductValidation.class})
    @Size(min = 10, max = 2047, groups = {CreateProductValidation.class})
    private String answer;

    public interface CreateProductValidation {
    }
}
