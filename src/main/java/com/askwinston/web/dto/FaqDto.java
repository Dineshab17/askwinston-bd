package com.askwinston.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
