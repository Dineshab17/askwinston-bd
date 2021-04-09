package com.askwinston.web.controller;

import com.askwinston.helper.ParsingHelper;
import com.askwinston.model.User;
import com.askwinston.repository.UserRepository;
import com.askwinston.service.UserService;
import com.askwinston.web.dto.GoogleLoginDto;
import com.askwinston.web.dto.TokenDto;
import com.askwinston.web.dto.UserDto;
import com.askwinston.web.secuity.JwtService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private ParsingHelper parsingHelper;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthController(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService,
                          ParsingHelper parsingHelper, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.parsingHelper = parsingHelper;
        this.userService = userService;
    }


    /**
     * @param userDto
     * @return TokenDto
     * To authenticate the user's information like username and password
     * and allow the user to access the application
     */
    @PostMapping("/login")
    public TokenDto login(@RequestBody UserDto userDto) {
        log.info("Searching for a user {} ", userDto.getEmail());
        List<User> users = userRepository.findByEmail(userDto.getEmail());
        if (!users.isEmpty()) {
            User user = users.get(0);
            if (passwordEncoder.matches(userDto.getPassword(), user.getPassword())) {
                String token = jwtService.createToken(user.getId(), user.getEmail(), user.getAuthority());
                userDto = parsingHelper.mapObject(user, UserDto.class);
                return new TokenDto(token, userDto);
            }
        }
        log.error("Unauthorized User {} ", userDto.getEmail());
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    /**
     * @param googleLoginDto
     * @return
     * This method is to login or signup with google account
     */
    @SneakyThrows
    @PostMapping(value = "/google")
    public TokenDto loginWithGoogle(@RequestBody GoogleLoginDto googleLoginDto){
        User user = this.userService.addGoogleUser(googleLoginDto);
        String token = jwtService.createToken(user.getId(), user.getEmail(), user.getAuthority());
        UserDto userDto = parsingHelper.mapObject(user, UserDto.class);
        return new TokenDto(token, userDto);
    }
}
