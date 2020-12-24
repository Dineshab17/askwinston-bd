package com.askwinston.web.secuity;

import com.askwinston.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

/*
    Class that creates and validates JWT-tokens
 */

@Service
public class JwtService {
    private static final String AUTHORITY_CLAIM = "Authority";
    private static final String ID_CLAIM = "Id";

    @Value("${askwinston.security.jwt.secret}")
    private String secret;

    @Value("${askwinston.security.jwt.expire-in-seconds}")
    private Long expirationTime;

    public String createToken(Long id, String email, User.Authority authority) {
        Date notBefore = Date.from(Instant.now());
        Date expiresAt = Date.from(Instant.now().plusSeconds(expirationTime));

        JWTCreator.Builder jwtTokenBuilder = JWT.create().withSubject(email);
        if (id != null) {
            jwtTokenBuilder.withClaim(ID_CLAIM, id);
        }
        if (authority != null) {
            jwtTokenBuilder.withClaim(AUTHORITY_CLAIM, authority.name());
        }

        return jwtTokenBuilder
                .withExpiresAt(expiresAt)
                .withNotBefore(notBefore)
                .sign(HMAC512(secret.getBytes()));
    }

    public AwUserPrincipal parseToken(String token) {
        DecodedJWT jwt = JWT.require(HMAC512(secret.getBytes())).build().verify(token);
        if (Instant.now().isBefore(jwt.getNotBefore().toInstant())) {
            throw new TokenExpiredException("expired");
        }
        if (Instant.now().isAfter(jwt.getExpiresAt().toInstant())) {
            throw new TokenExpiredException("expired");
        }
        return new AwUserPrincipal(jwt.getClaim(ID_CLAIM).asLong(), jwt.getSubject(), jwt.getClaim(AUTHORITY_CLAIM).asString());
    }

}
