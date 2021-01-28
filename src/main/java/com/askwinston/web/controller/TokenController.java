package com.askwinston.web.controller;

import com.askwinston.model.Token;
import com.askwinston.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@Slf4j
@RefreshScope
public class TokenController {

    @Value("${askwinston.token.reset-password.expire-in-seconds}")
    private int resetPasswordTokenExpirationSeconds;

    private TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * @param tokenId
     * To validate the provided token
     */
    @GetMapping("/{tokenId}/validate")
    public void validateToken(@PathVariable("tokenId") String tokenId) {
        Token token = tokenService.getTokenById(tokenId);

        String errorMessage = "The link is no longer valid";
        if (token == null) {
            log.error("Provided token is null");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
        if (token.getType().equals(Token.Type.PASSWORD_RESET)) {
            log.info("Validating token for type: {}", Token.Type.PASSWORD_RESET);
            if (token.getUserId() == null) {
                log.error("Token doesn't have user id");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
            }
            if (token.getCreationDate()
                    .before(Date.from(Instant.now().minusSeconds(resetPasswordTokenExpirationSeconds)))) {
                log.error("Token has been expired");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
            }
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMessage);
        }
    }
}
