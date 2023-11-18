package com.askwinston.web.dto;

import com.askwinston.model.User;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @JsonView(DtoView.UserVisibility.class)
    private Long id;

    @Email(groups = {CreatePatientValidation.class, UpdatePatientValidation.class})
    @NotNull(groups = {CreatePatientValidation.class})
    @JsonView({DtoView.PatientVisibility.class, DtoView.PharmacistVisibility.class, DtoView.AdminVisibility.class})
    private String email;

    @NotNull(groups = {CreatePatientValidation.class})
    @Size(min = 6, max = 255, groups = {CreatePatientValidation.class, UpdatePatientValidation.class})
    @JsonView(DtoView.HiddenVisibility.class)
    private String password;

//    @NotNull(groups = {CreatePatientValidation.class})
    @Size(min = 1, max = 255, groups = {CreatePatientValidation.class, UpdatePatientValidation.class})
    @JsonView(DtoView.UserVisibility.class)
    private String firstName;

//    @NotNull(groups = {CreatePatientValidation.class})
    @Size(min = 1, max = 255, groups = {CreatePatientValidation.class, UpdatePatientValidation.class})
    @JsonView(DtoView.UserVisibility.class)
    private String lastName;

//    @NotNull(groups = {CreatePatientValidation.class})
    @Pattern(regexp = "[\\d\\+\\s-\\(\\)]{5,64}", groups = {CreatePatientValidation.class, UpdatePatientValidation.class})
    @JsonView(DtoView.UserVisibility.class)
    private String phone;

//    @NotNull(groups = {CreatePatientValidation.class})
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", groups = {CreatePatientValidation.class, UpdatePatientValidation.class})
    @JsonView(DtoView.UserVisibility.class)
    private String birthday;

    @JsonView(DtoView.PatientVisibility.class)
    private List<BillingCardDto> billingCards;

    @JsonView(DtoView.PatientVisibility.class)
    private List<ShippingAddressDto> shippingAddresses;

    @JsonView(DtoView.PatientVisibility.class)
    private String province;

    @JsonView(DtoView.PatientVisibility.class)
    private User.Authority authority;

    @JsonView(DtoView.PatientVisibility.class)
    private String timezone;

    @JsonView(DtoView.UserVisibility.class)
    private Long idDocument;

    @JsonView(DtoView.UserVisibility.class)
    private Long insuranceDocument;

    @JsonView(DtoView.UserVisibility.class)
    private String utmSource;

    @JsonView(DtoView.UserVisibility.class)
    private String socialLoginSource;


    public interface CreatePatientValidation {
    }

    public interface UpdatePatientValidation {
    }
}
