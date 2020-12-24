package com.askwinston.service;

import com.askwinston.model.Token;

public interface TokenService {
    String createPharmacistConsultToken(Long userId, Long orderId);

    String createResetPasswordToken(Long userId);

    Token getTokenById(String id);

    void deleteTokenById(String id);

    boolean hasTokenExpired(Token token);
}
