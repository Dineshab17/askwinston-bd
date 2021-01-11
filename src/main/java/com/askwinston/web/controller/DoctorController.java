package com.askwinston.web.controller;

import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.User;
import com.askwinston.service.UserService;
import com.askwinston.web.dto.DtoView;
import com.askwinston.web.dto.UserDto;
import com.askwinston.web.secuity.AwUserPrincipal;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/doctor")
public class DoctorController {
    private final UserService userService;
    private ParsingHelper parsingHelper;

    public DoctorController(UserService userService, ParsingHelper parsingHelper) {
        this.userService = userService;
        this.parsingHelper = parsingHelper;
    }

    /**
     * @param userDto
     * @param principal
     * @return UserDto
     * To update profile information of the doctor
     */
    @PutMapping("/profile")
    @PreAuthorize("hasAnyAuthority('DOCTOR')")
    @JsonView(DtoView.PatientVisibility.class)
    public UserDto updateMyProfile(@Validated(UserDto.UpdatePatientValidation.class) @RequestBody UserDto userDto,
                                   @AuthenticationPrincipal AwUserPrincipal principal) {
        userDto.setId(principal.getId());
        User updatedUser = userService.update(parsingHelper.mapObject(userDto, User.class));
        return parsingHelper.mapObject(updatedUser, UserDto.class);
    }

}
