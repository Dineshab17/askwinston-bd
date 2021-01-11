package com.askwinston.web.controller;

import com.askwinston.exception.UserException;
import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.Token;
import com.askwinston.model.User;
import com.askwinston.service.TokenService;
import com.askwinston.service.UserService;
import com.askwinston.web.dto.ChangePasswordDto;
import com.askwinston.web.dto.DtoView;
import com.askwinston.web.dto.TextDto;
import com.askwinston.web.dto.UserDto;
import com.askwinston.web.secuity.AwUserPrincipal;
import com.askwinston.web.secuity.JwtService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private ParsingHelper parsingHelper;
    private TokenService tokenService;
    private JwtService jwtService;

    @Value("${askwinston.token.reset-password.expire-in-seconds}")
    private int resetPasswordTokenExpirationSeconds;

    public UserController(UserService userService, ParsingHelper parsingHelper, TokenService tokenService,
                          JwtService jwtService) {
        this.userService = userService;
        this.parsingHelper = parsingHelper;
        this.tokenService = tokenService;
        this.jwtService = jwtService;
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'PHARMACIST', 'ADMIN')")
    @JsonView(DtoView.PatientVisibility.class)
    public UserDto getMyProfile(@AuthenticationPrincipal AwUserPrincipal principal) {
        User user = userService.getById(principal.getId());
        return parsingHelper.mapObject(user, UserDto.class);
    }

    @GetMapping("/{id}/profile")
    @PreAuthorize("hasAnyAuthority('DOCTOR', 'PHARMACIST')")
    @JsonView(DtoView.PatientVisibility.class)
    public UserDto getProfile(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        return parsingHelper.mapObject(user, UserDto.class);
    }

    @PostMapping("/forgot-password/{email}")
    public void forgotPassword(@PathVariable("email") String email) {
        try {
            userService.sendResetPasswordEmail(email);
        } catch (UserException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/change-password")
    @PreAuthorize("hasAnyAuthority('PATIENT', 'DOCTOR', 'PHARMACIST', 'ADMIN')")
    public void changePassword(@RequestBody ChangePasswordDto dto, @AuthenticationPrincipal AwUserPrincipal principal) {
        try {
            userService.changePassword(principal.getId(), dto.getOldPassword(), dto.getNewPassword());
        } catch (UserException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/reset-password/{token}")
    public String resetPassword(@PathVariable("token") String tokenId, @RequestBody TextDto passwordDto) {
        Token token = tokenService.getTokenById(tokenId);

        String errorMessage = "The link is no longer valid";
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        if (token.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        if (!token.getType().equals(Token.Type.PASSWORD_RESET)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        if (token.getCreationDate()
                .before(Date.from(Instant.now().minusSeconds(resetPasswordTokenExpirationSeconds)))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }

        User user = userService.getById(token.getUserId());
        userService.resetPassword(user.getId(), passwordDto.getText());
        tokenService.deleteTokenById(tokenId);

        return jwtService.createToken(user.getId(), user.getEmail(), user.getAuthority());
    }
}
