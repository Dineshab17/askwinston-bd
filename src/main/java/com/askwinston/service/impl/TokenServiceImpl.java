package com.askwinston.service.impl;

import com.askwinston.model.Token;
import com.askwinston.repository.TokenRepository;
import com.askwinston.service.TokenService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {
    private static final long PHARMACIST_CONSULT_TOKEN_EXPIRE_AFTER_SECONDS = 259200; // 3 days

    private TokenRepository tokenRepository;

    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

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
        return token.getId();
    }

    @Override
    public String createResetPasswordToken(Long userId) {
        Token token = Token.builder()
                .id(UUID.randomUUID().toString())
                .creationDate(Date.from(Instant.now()))
                .type(Token.Type.PASSWORD_RESET)
                .userId(userId)
                .build();
        tokenRepository.save(token);
        return token.getId();
    }

    @Override
    public Token getTokenById(String id) {
        return tokenRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteTokenById(String id) {
        tokenRepository.deleteById(id);
    }

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
