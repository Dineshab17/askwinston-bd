package com.askwinston.web.controller;

import com.askwinston.model.Token;
import com.askwinston.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Date;

@RestController
@RequestMapping("/token")
public class TokenController {

    @Value("${askwinston.token.reset-password.expire-in-seconds}")
    private int RESET_PASSWORD_TOKEN_EXPIRATION_SECONDS;

    private TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/{tokenId}/validate")
    public void validateToken(@PathVariable("tokenId") String tokenId) {
        Token token = tokenService.getTokenById(tokenId);

        String errorMessage = "The link is no longer valid";
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        if (token.getType().equals(Token.Type.PASSWORD_RESET)) {
            if (token.getUserId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
            }
            if (token.getCreationDate()
                    .before(Date.from(Instant.now().minusSeconds(RESET_PASSWORD_TOKEN_EXPIRATION_SECONDS)))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }
}
