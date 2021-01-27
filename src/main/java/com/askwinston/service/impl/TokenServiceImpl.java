package com.askwinston.service.impl;

import com.askwinston.model.Token;
import com.askwinston.repository.TokenRepository;
import com.askwinston.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class TokenServiceImpl implements TokenService {
    private static final long PHARMACIST_CONSULT_TOKEN_EXPIRE_AFTER_SECONDS = 259200; // 3 days

    private TokenRepository tokenRepository;

    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    /**
     * @param userId
     * @param orderId
     * @return String
     * To create token for pharmacist consultation
     */
    @Override
    public String createPharmacistConsultToken(Long userId, Long orderId) {
        Token token = Token.builder()
                .id(UUID.randomUUID().toString())
                .creationDate(Date.from(Instant.now()))
                .type(Token.Type.PHARMACIST_CONSULT)
                .userId(userId)
                .orderId(orderId)
                .build();
        tokenRepository.save(token);
        log.info("Token has been created for pharmacist consult for the use with id {}", userId);
        return token.getId();
    }

    /**
     * @param userId
     * @return String
     * To generate Token for reset password
     */
    @Override
    public String createResetPasswordToken(Long userId) {
        Token token = Token.builder()
                .id(UUID.randomUUID().toString())
                .creationDate(Date.from(Instant.now()))
                .type(Token.Type.PASSWORD_RESET)
                .userId(userId)
                .build();
        tokenRepository.save(token);
        log.info("Reset password token has been generated for the user with id {}", userId);
        return token.getId();
    }

    /**
     * @param id
     * @return Token
     * To get the Token by id
     */
    @Override
    public Token getTokenById(String id) {
        return tokenRepository.findById(id).orElse(null);
    }

    /**
     * @param id
     * To delete token from table
     */
    @Override
    public void deleteTokenById(String id) {
        tokenRepository.deleteById(id);
    }

    /**
     * @param token
     * @return boolean
     * To check whether the token expired
     */
    @Override
    public boolean hasTokenExpired(Token token) {
        if (token.getType().equals(Token.Type.PHARMACIST_CONSULT)) {
            return token.getCreationDate().toInstant()
                    .plusSeconds(PHARMACIST_CONSULT_TOKEN_EXPIRE_AFTER_SECONDS)
                    .isBefore(Instant.now());
        }
        return false;
    }
}
